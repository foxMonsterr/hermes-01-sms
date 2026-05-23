package com.snack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.snack.common.BusinessException;
import com.snack.common.PageData;
import com.snack.dto.*;
import com.snack.entity.*;
import com.snack.mapper.*;
import com.snack.service.ShoppingListService;
import com.snack.vo.ShoppingListVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {

    private final ShoppingListMapper shoppingListMapper;
    private final SnackMapper snackMapper;
    private final CategoryMapper categoryMapper;
    private final StockRecordMapper stockRecordMapper;
    private final SupplierMapper supplierMapper;

    public ShoppingListServiceImpl(ShoppingListMapper shoppingListMapper,
                                    SnackMapper snackMapper,
                                    CategoryMapper categoryMapper,
                                    StockRecordMapper stockRecordMapper,
                                    SupplierMapper supplierMapper) {
        this.shoppingListMapper = shoppingListMapper;
        this.snackMapper = snackMapper;
        this.categoryMapper = categoryMapper;
        this.stockRecordMapper = stockRecordMapper;
        this.supplierMapper = supplierMapper;
    }

    @Override
    public PageData<ShoppingListVO> page(Long currentUserId, Integer page, Integer size,
                                          String status, String keyword) {
        Page<ShoppingList> mpPage = new Page<>(page != null ? page : 1, size != null ? size : 10);

        LambdaQueryWrapper<ShoppingList> wrapper = new LambdaQueryWrapper<ShoppingList>()
                .eq(ShoppingList::getUserId, currentUserId);

        if (status != null && !status.isBlank()) {
            if (!status.matches("PENDING|BOUGHT|CANCELLED")) {
                throw BusinessException.badRequest("status 参数不合法: " + status);
            }
            wrapper.eq(ShoppingList::getStatus, status);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(ShoppingList::getSnackName, keyword);
        }
        wrapper.orderByDesc(ShoppingList::getCreateTime);

        Page<ShoppingList> result = shoppingListMapper.selectPage(mpPage, wrapper);
        List<ShoppingListVO> records = result.getRecords().stream().map(this::toVO).toList();

        PageData<ShoppingListVO> pageData = new PageData<>();
        pageData.setRecords(records);
        pageData.setTotal(result.getTotal());
        pageData.setPage(result.getCurrent());
        pageData.setSize(result.getSize());
        return pageData;
    }

    @Override
    public ShoppingListVO create(Long currentUserId, ShoppingListCreateDTO dto) {
        // 校验 snack_id 属于当前用户
        if (dto.getSnackId() != null) {
            validateSnackOwnership(currentUserId, dto.getSnackId());
        }
        if (dto.getCategoryId() != null) {
            validateCategoryOwnership(currentUserId, dto.getCategoryId());
        }
        if (dto.getPrice() != null && dto.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw BusinessException.badRequest("价格不能为负数");
        }
        // 校验供应商
        if (dto.getSupplierId() != null) {
            Supplier supplier = supplierMapper.selectById(dto.getSupplierId());
            if (supplier == null || !supplier.getUserId().equals(currentUserId))
                throw BusinessException.notFound("供应商不存在");
        }

        ShoppingList sl = new ShoppingList();
        sl.setUserId(currentUserId);
        sl.setSnackId(dto.getSnackId());
        sl.setSnackName(dto.getSnackName());
        sl.setCategoryId(dto.getCategoryId());
        sl.setPlannedQty(dto.getPlannedQty());
        sl.setPrice(dto.getPrice());
        sl.setSource("MANUAL");
        sl.setRemark(dto.getRemark());
        sl.setStatus("PENDING");
        // 供应商快照
        if (dto.getSupplierId() != null) {
            sl.setSupplierId(dto.getSupplierId());
            Supplier sup = supplierMapper.selectById(dto.getSupplierId());
            sl.setSupplierName(sup != null ? sup.getName() : "");
        }
        shoppingListMapper.insert(sl);
        return toVO(sl);
    }

    @Override
    public ShoppingListVO update(Long currentUserId, Long id, ShoppingListUpdateDTO dto) {
        ShoppingList sl = findOwn(currentUserId, id);
        if (!"PENDING".equals(sl.getStatus())) {
            throw BusinessException.badRequest("只有待采购状态的清单项可以编辑");
        }
        if (dto.getCategoryId() != null) {
            validateCategoryOwnership(currentUserId, dto.getCategoryId());
        }

        sl.setSnackName(dto.getSnackName());
        sl.setCategoryId(dto.getCategoryId() != null ? dto.getCategoryId() : sl.getCategoryId());
        sl.setPlannedQty(dto.getPlannedQty() != null ? dto.getPlannedQty() : sl.getPlannedQty());
        sl.setPrice(dto.getPrice() != null ? dto.getPrice() : sl.getPrice());
        sl.setRemark(dto.getRemark() != null ? dto.getRemark() : sl.getRemark());
        shoppingListMapper.updateById(sl);
        return toVO(sl);
    }

    @Override
    public void updateStatus(Long currentUserId, Long id, ShoppingListStatusDTO dto) {
        ShoppingList sl = findOwn(currentUserId, id);
        if (!"PENDING".equals(sl.getStatus())) {
            throw BusinessException.badRequest("只有待采购状态的清单项可以修改状态");
        }
        if (!"CANCELLED".equals(dto.getStatus())) {
            throw BusinessException.badRequest("状态接口只允许将 PENDING 改为 CANCELLED");
        }
        sl.setStatus("CANCELLED");
        shoppingListMapper.updateById(sl);
    }

    @Override
    public void delete(Long currentUserId, Long id) {
        findOwn(currentUserId, id);
        shoppingListMapper.deleteById(id);
    }

    @Override
    public Map<String, Integer> fromLowStock(Long currentUserId) {
        // 查找低库存零食 (quantity ≤ 2)
        List<Snack> lowSnacks = snackMapper.selectList(
                new LambdaQueryWrapper<Snack>()
                        .eq(Snack::getUserId, currentUserId)
                        .le(Snack::getQuantity, 2)
        );

        int created = 0;
        int skipped = 0;

        for (Snack s : lowSnacks) {
            // 检查是否已有 PENDING 的相同 snack_id
            boolean exists = shoppingListMapper.exists(
                    new LambdaQueryWrapper<ShoppingList>()
                            .eq(ShoppingList::getUserId, currentUserId)
                            .eq(ShoppingList::getSnackId, s.getId())
                            .eq(ShoppingList::getStatus, "PENDING")
            );
            if (exists) {
                skipped++;
                continue;
            }

            ShoppingList sl = new ShoppingList();
            sl.setUserId(currentUserId);
            sl.setSnackId(s.getId());
            sl.setSnackName(s.getName());
            sl.setCategoryId(s.getCategoryId());
            sl.setPlannedQty(1);
            sl.setPrice(s.getPrice());
            sl.setSource("LOW_STOCK");
            sl.setStatus("PENDING");
            sl.setRemark("低库存自动生成 (当前库存: " + s.getQuantity() + ")");
            shoppingListMapper.insert(sl);
            created++;
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("createdCount", created);
        result.put("skippedCount", skipped);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> stockIn(Long currentUserId, Long id, ShoppingListStockInDTO dto) {
        ShoppingList sl = findOwn(currentUserId, id);
        if (!"PENDING".equals(sl.getStatus())) {
            throw BusinessException.badRequest("只有待采购状态的清单项可以入库");
        }

        // 匹配或创建零食
        Snack snack = null;
        boolean isNew = false;

        if (sl.getSnackId() != null) {
            snack = snackMapper.selectById(sl.getSnackId());
            if (snack == null || !snack.getUserId().equals(currentUserId)) {
                throw BusinessException.notFound("关联零食不存在");
            }
        } else {
            // 按名称 + categoryId 匹配
            LambdaQueryWrapper<Snack> wrapper = new LambdaQueryWrapper<Snack>()
                    .eq(Snack::getUserId, currentUserId)
                    .eq(Snack::getName, sl.getSnackName());
            if (sl.getCategoryId() != null) {
                wrapper.eq(Snack::getCategoryId, sl.getCategoryId());
            }
            snack = snackMapper.selectOne(wrapper);
        }

        Long categoryId = dto.getCategoryId() != null ? dto.getCategoryId() : sl.getCategoryId();

        if (snack == null) {
            // 创建新零食
            if (categoryId == null) {
                throw BusinessException.badRequest("请选择分类后再入库");
            }
            validateCategoryOwnership(currentUserId, categoryId);

            snack = new Snack();
            snack.setUserId(currentUserId);
            snack.setName(sl.getSnackName());
            snack.setCategoryId(categoryId);
            snack.setQuantity(0);
            snack.setUnit("包");
            snack.setPrice(dto.getPrice() != null ? dto.getPrice() : sl.getPrice());
            snackMapper.insert(snack);
            isNew = true;
        } else {
            // categoryId 不修改已有零食分类
            if (dto.getPrice() != null) {
                snack.setPrice(dto.getPrice());
            }
        }

        int oldQty = snack.getQuantity();
        int newQty = oldQty + dto.getActualQty();
        snack.setQuantity(newQty);
        snackMapper.updateById(snack);

        // 生成库存流水
        StockRecord sr = new StockRecord();
        sr.setUserId(currentUserId);
        sr.setSnackId(snack.getId());
        sr.setSnackName(snack.getName());
        sr.setChangeType(isNew ? "INIT" : "IN");
        sr.setChangeQty(dto.getActualQty());
        sr.setBeforeQty(oldQty);
        sr.setAfterQty(newQty);
        sr.setRemark(isNew ? "采购清单入库创建零食" : "采购清单入库");
        stockRecordMapper.insert(sr);

        // 更新清单状态
        sl.setStatus("BOUGHT");
        sl.setActualQty(dto.getActualQty());
        sl.setBoughtTime(LocalDateTime.now());
        shoppingListMapper.updateById(sl);

        Map<String, Object> result = new HashMap<>();
        result.put("shoppingListId", id);
        result.put("snackId", snack.getId());
        result.put("status", "BOUGHT");
        result.put("quantity", newQty);
        return result;
    }

    // ---------- helpers ----------

    private ShoppingList findOwn(Long currentUserId, Long id) {
        ShoppingList sl = shoppingListMapper.selectById(id);
        if (sl == null || !sl.getUserId().equals(currentUserId)) {
            throw BusinessException.notFound("清单项不存在");
        }
        return sl;
    }

    private void validateSnackOwnership(Long currentUserId, Long snackId) {
        Snack snack = snackMapper.selectById(snackId);
        if (snack == null || !snack.getUserId().equals(currentUserId)) {
            throw BusinessException.notFound("零食不存在");
        }
    }

    private void validateCategoryOwnership(Long currentUserId, Long categoryId) {
        Category cat = categoryMapper.selectById(categoryId);
        if (cat == null || !cat.getUserId().equals(currentUserId)) {
            throw BusinessException.notFound("分类不存在");
        }
    }

    private ShoppingListVO toVO(ShoppingList sl) {
        ShoppingListVO vo = new ShoppingListVO();
        vo.setId(sl.getId());
        vo.setSnackId(sl.getSnackId());
        vo.setSnackName(sl.getSnackName());
        vo.setCategoryId(sl.getCategoryId());
        vo.setPlannedQty(sl.getPlannedQty());
        vo.setPrice(sl.getPrice());
        vo.setStatus(sl.getStatus());
        vo.setSource(sl.getSource());
        vo.setSupplierId(sl.getSupplierId());
        vo.setSupplierName(sl.getSupplierName());
        vo.setActualQty(sl.getActualQty());
        vo.setBoughtTime(sl.getBoughtTime());
        vo.setRemark(sl.getRemark());
        vo.setCreateTime(sl.getCreateTime());

        if (sl.getCategoryId() != null) {
            Category cat = categoryMapper.selectById(sl.getCategoryId());
            vo.setCategoryName(cat != null ? cat.getName() : "");
        }
        return vo;
    }
}
