package com.snack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.snack.common.BusinessException;
import com.snack.common.PageData;
import com.snack.dto.QuantityAdjustDTO;
import com.snack.dto.QuantityUpdateDTO;
import com.snack.dto.SnackCreateDTO;
import com.snack.dto.SnackQueryDTO;
import com.snack.dto.SnackUpdateDTO;
import com.snack.entity.Category;
import com.snack.entity.Snack;
import com.snack.entity.StockRecord;
import com.snack.mapper.CategoryMapper;
import com.snack.mapper.SnackMapper;
import com.snack.mapper.StockRecordMapper;
import com.snack.service.SnackService;
import com.snack.vo.QuantityUpdateVO;
import com.snack.vo.SnackPageVO;
import com.snack.vo.SnackVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 零食服务实现（用户隔离）
 */
@Service
public class SnackServiceImpl implements SnackService {

    private final SnackMapper snackMapper;
    private final CategoryMapper categoryMapper;
    private final StockRecordMapper stockRecordMapper;

    public SnackServiceImpl(SnackMapper snackMapper, CategoryMapper categoryMapper,
                            StockRecordMapper stockRecordMapper) {
        this.snackMapper = snackMapper;
        this.categoryMapper = categoryMapper;
        this.stockRecordMapper = stockRecordMapper;
    }

    @Override
    public PageData<SnackPageVO> page(Long currentUserId, SnackQueryDTO query) {
        Page<Snack> mpPage = new Page<>(query.getPage(), query.getSize());

        LambdaQueryWrapper<Snack> wrapper = new LambdaQueryWrapper<Snack>()
                .eq(Snack::getUserId, currentUserId);

        // 关键字模糊搜索
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.like(Snack::getName, query.getKeyword());
        }

        // 分类筛选
        if (query.getCategoryId() != null) {
            wrapper.eq(Snack::getCategoryId, query.getCategoryId());
        }

        // 过期状态筛选
        applyExpiryFilter(wrapper, query.getExpiryStatus());

        wrapper.orderByDesc(Snack::getCreateTime);

        Page<Snack> result = snackMapper.selectPage(mpPage, wrapper);

        // entity → VO，填充 categoryName + expiryStatus
        List<SnackPageVO> records = result.getRecords().stream()
                .map(this::toPageVO)
                .toList();

        PageData<SnackPageVO> pageData = new PageData<>();
        pageData.setRecords(records);
        pageData.setTotal(result.getTotal());
        pageData.setPage(result.getCurrent());
        pageData.setSize(result.getSize());
        return pageData;
    }

    @Override
    public SnackVO getDetail(Long currentUserId, Long id) {
        Snack snack = findOwnSnack(currentUserId, id);
        return toVO(snack);
    }

    @Override
    public SnackVO create(Long currentUserId, SnackCreateDTO dto) {
        // 校验分类属于当前用户
        validateCategory(currentUserId, dto.getCategoryId());

        Snack snack = new Snack();
        snack.setUserId(currentUserId);
        snack.setName(dto.getName());
        snack.setCategoryId(dto.getCategoryId());
        snack.setQuantity(dto.getQuantity());
        snack.setUnit(dto.getUnit() != null ? dto.getUnit() : "包");
        snack.setPurchaseDate(dto.getPurchaseDate());
        snack.setExpiryDate(dto.getExpiryDate());
        snack.setNotes(dto.getNotes() != null ? dto.getNotes() : "");
        snack.setPrice(dto.getPrice());

        snackMapper.insert(snack);

        // 生成 INIT 流水
        StockRecord sr = new StockRecord();
        sr.setUserId(currentUserId);
        sr.setSnackId(snack.getId());
        sr.setSnackName(snack.getName());
        sr.setChangeType("INIT");
        sr.setChangeQty(snack.getQuantity());
        sr.setBeforeQty(0);
        sr.setAfterQty(snack.getQuantity());
        sr.setRemark("新增零食");
        stockRecordMapper.insert(sr);

        return toVO(snack);
    }

    @Override
    public SnackVO update(Long currentUserId, Long id, SnackUpdateDTO dto) {
        Snack snack = findOwnSnack(currentUserId, id);

        // 如果修改了分类，校验分类属于当前用户
        if (!dto.getCategoryId().equals(snack.getCategoryId())) {
            validateCategory(currentUserId, dto.getCategoryId());
        }

        snack.setName(dto.getName());
        snack.setCategoryId(dto.getCategoryId());
        snack.setUnit(dto.getUnit() != null ? dto.getUnit() : snack.getUnit());
        snack.setPrice(dto.getPrice() != null ? dto.getPrice() : snack.getPrice());
        snack.setPurchaseDate(dto.getPurchaseDate());
        snack.setExpiryDate(dto.getExpiryDate());
        snack.setNotes(dto.getNotes() != null ? dto.getNotes() : snack.getNotes());
        snack.setImageUrl(dto.getImageUrl() != null ? dto.getImageUrl() : snack.getImageUrl());

        snackMapper.updateById(snack);
        return toVO(snack);
    }

    @Override
    public QuantityUpdateVO updateQuantity(Long currentUserId, Long id, QuantityUpdateDTO dto) {
        if (dto.getDelta() == 0) {
            throw BusinessException.badRequest("变更量不能为 0");
        }

        Snack snack = findOwnSnack(currentUserId, id);
        int oldQty = snack.getQuantity();
        int newQuantity = oldQty + dto.getDelta();

        if (newQuantity < 0) {
            throw BusinessException.badRequest("库存不足，当前库存为 " + oldQty);
        }

        snack.setQuantity(newQuantity);
        snackMapper.updateById(snack);

        // 生成库存流水
        String changeType = dto.getDelta() > 0 ? "IN" : "OUT";
        StockRecord sr = new StockRecord();
        sr.setUserId(currentUserId);
        sr.setSnackId(snack.getId());
        sr.setSnackName(snack.getName());
        sr.setChangeType(changeType);
        sr.setChangeQty(dto.getDelta());
        sr.setBeforeQty(oldQty);
        sr.setAfterQty(newQuantity);
        sr.setRemark(dto.getDelta() > 0 ? "快速入库" : "日常消耗");
        stockRecordMapper.insert(sr);

        return new QuantityUpdateVO(snack.getId(), newQuantity);
    }

    /** 盘点调整库存（v4.0 新增） */
    public QuantityUpdateVO adjustQuantity(Long currentUserId, Long id, QuantityAdjustDTO dto) {
        Snack snack = findOwnSnack(currentUserId, id);
        int oldQty = snack.getQuantity();
        int newQty = dto.getQuantity();
        int changeQty = newQty - oldQty;

        if (changeQty != 0) {
            snack.setQuantity(newQty);
            snackMapper.updateById(snack);

            StockRecord sr = new StockRecord();
            sr.setUserId(currentUserId);
            sr.setSnackId(snack.getId());
            sr.setSnackName(snack.getName());
            sr.setChangeType("ADJUST");
            sr.setChangeQty(changeQty);
            sr.setBeforeQty(oldQty);
            sr.setAfterQty(newQty);
            sr.setRemark(dto.getRemark() != null ? dto.getRemark() : "盘点调整");
            stockRecordMapper.insert(sr);
        }

        return new QuantityUpdateVO(snack.getId(), snack.getQuantity());
    }

    @Override
    public void delete(Long currentUserId, Long id) {
        findOwnSnack(currentUserId, id);
        snackMapper.deleteById(id);
    }

    @Override
    public void batchDelete(Long currentUserId, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        // 查询这些 id 中属于当前用户的未删除零食
        List<Snack> snacks = snackMapper.selectList(
                new LambdaQueryWrapper<Snack>()
                        .eq(Snack::getUserId, currentUserId)
                        .in(Snack::getId, ids)
        );

        // 有 id 不属于当前用户 → 404
        if (snacks.size() != ids.size()) {
            throw BusinessException.notFound("部分零食不存在或无权操作");
        }

        // 批量逻辑删除
        for (Snack snack : snacks) {
            snackMapper.deleteById(snack.getId());
        }
    }

    // ---------- private helpers ----------

    /**
     * 查找当前用户自己的零食，不存在 → 404
     */
    private Snack findOwnSnack(Long currentUserId, Long id) {
        Snack snack = snackMapper.selectById(id);
        if (snack == null || !snack.getUserId().equals(currentUserId)) {
            throw BusinessException.notFound("资源不存在");
        }
        return snack;
    }

    /**
     * 校验分类存在且属于当前用户
     */
    private void validateCategory(Long currentUserId, Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null || !category.getUserId().equals(currentUserId)) {
            throw BusinessException.notFound("分类不存在");
        }
    }

    /**
     * Snack → SnackVO (带 categoryName)
     */
    private SnackVO toVO(Snack snack) {
        SnackVO vo = new SnackVO();
        fillCommon(vo, snack);
        return vo;
    }

    /**
     * Snack → SnackPageVO
     */
    private SnackPageVO toPageVO(Snack snack) {
        SnackPageVO vo = new SnackPageVO();
        fillCommon(vo, snack);
        return vo;
    }

    private void fillCommon(SnackVO vo, Snack snack) {
        vo.setId(snack.getId());
        vo.setName(snack.getName());
        vo.setCategoryId(snack.getCategoryId());
        vo.setQuantity(snack.getQuantity());
        vo.setUnit(snack.getUnit());
        vo.setPrice(snack.getPrice());
        vo.setImageUrl(snack.getImageUrl());

        // totalPrice = price × quantity（price 为 null 时 totalPrice 为 null）
        if (snack.getPrice() != null) {
            vo.setTotalPrice(snack.getPrice().multiply(
                    java.math.BigDecimal.valueOf(snack.getQuantity())));
        }

        vo.setPurchaseDate(snack.getPurchaseDate());
        vo.setExpiryDate(snack.getExpiryDate());
        vo.setNotes(snack.getNotes());
        vo.setCreateTime(snack.getCreateTime());

        // 分类名
        Category category = categoryMapper.selectById(snack.getCategoryId());
        vo.setCategoryName(category != null ? category.getName() : "");

        // 过期状态
        if (snack.getExpiryDate() == null) {
            vo.setExpiryStatus("unknown");
            vo.setDaysUntilExpiry(null);
        } else {
            long days = ChronoUnit.DAYS.between(LocalDate.now(), snack.getExpiryDate());
            vo.setDaysUntilExpiry(days);
            if (days < 0) {
                vo.setExpiryStatus("expired");
            } else if (days <= 30) {
                vo.setExpiryStatus("soon");
            } else {
                vo.setExpiryStatus("fresh");
            }
        }
    }

    /**
     * 过期状态筛选 —— 通过 SQL 条件实现
     */
    private void applyExpiryFilter(LambdaQueryWrapper<Snack> wrapper, String expiryStatus) {
        if (expiryStatus == null || expiryStatus.isBlank()) {
            return;
        }
        LocalDate today = LocalDate.now();
        switch (expiryStatus) {
            case "expired":
                wrapper.isNotNull(Snack::getExpiryDate)
                       .lt(Snack::getExpiryDate, today);
                break;
            case "soon":
                wrapper.isNotNull(Snack::getExpiryDate)
                       .ge(Snack::getExpiryDate, today)
                       .le(Snack::getExpiryDate, today.plusDays(30));
                break;
            case "fresh":
                wrapper.and(w -> w.isNull(Snack::getExpiryDate)
                        .or().gt(Snack::getExpiryDate, today.plusDays(30)));
                break;
        }
    }
}
