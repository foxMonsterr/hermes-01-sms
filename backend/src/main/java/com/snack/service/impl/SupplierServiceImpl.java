package com.snack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.snack.common.BusinessException;
import com.snack.common.PageData;
import com.snack.entity.ShoppingList;
import com.snack.entity.Supplier;
import com.snack.mapper.ShoppingListMapper;
import com.snack.mapper.SupplierMapper;
import com.snack.service.SupplierService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;

@Service
public class SupplierServiceImpl implements SupplierService {
    private final SupplierMapper mapper;
    private final ShoppingListMapper slMapper;

    public SupplierServiceImpl(SupplierMapper m, ShoppingListMapper sl) { this.mapper = m; this.slMapper = sl; }

    @Override
    public PageData<Supplier> page(Long userId, int page, int size, String keyword) {
        LambdaQueryWrapper<Supplier> w = new LambdaQueryWrapper<Supplier>().eq(Supplier::getUserId, userId);
        if (keyword != null && !keyword.isBlank()) w.like(Supplier::getName, keyword);
        w.orderByDesc(Supplier::getCreateTime);
        Page<Supplier> mp = mapper.selectPage(new Page<>(page, size), w);
        PageData<Supplier> pd = new PageData<>();
        pd.setRecords(mp.getRecords()); pd.setTotal(mp.getTotal()); pd.setPage(mp.getCurrent()); pd.setSize(mp.getSize());
        return pd;
    }

    @Override
    public List<Map<String, Object>> options(Long userId) {
        List<Supplier> list = mapper.selectList(new LambdaQueryWrapper<Supplier>().eq(Supplier::getUserId, userId).orderByAsc(Supplier::getName));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Supplier s : list) { Map<String, Object> m = new HashMap<>(); m.put("id", s.getId()); m.put("name", s.getName()); result.add(m); }
        return result;
    }

    @Override
    public Supplier getById(Long userId, Long id) { return findOwn(userId, id); }

    @Override
    public Supplier create(Long userId, Supplier s) {
        if (s.getName() == null || s.getName().isBlank()) throw BusinessException.badRequest("名称不能为空");
        boolean exists = mapper.exists(new LambdaQueryWrapper<Supplier>().eq(Supplier::getUserId, userId).eq(Supplier::getName, s.getName()));
        if (exists) throw BusinessException.conflict("供应商名称已存在");
        s.setUserId(userId);
        s.setId(null);
        mapper.insert(s);
        return s;
    }

    @Override
    public Supplier update(Long userId, Long id, Supplier s) {
        Supplier exist = findOwn(userId, id);
        boolean dup = mapper.exists(new LambdaQueryWrapper<Supplier>().eq(Supplier::getUserId, userId).eq(Supplier::getName, s.getName()).ne(Supplier::getId, id));
        if (dup) throw BusinessException.conflict("供应商名称已存在");
        exist.setName(s.getName()); exist.setContact(s.getContact()); exist.setPhone(s.getPhone());
        exist.setAddress(s.getAddress()); exist.setNotes(s.getNotes());
        mapper.updateById(exist);
        return exist;
    }

    @Override
    public void delete(Long userId, Long id) { findOwn(userId, id); mapper.deleteById(id); }

    @Override
    public Map<String, Object> stats(Long userId, Long id, String startDate, String endDate) {
        findOwn(userId, id);
        LambdaQueryWrapper<ShoppingList> w = new LambdaQueryWrapper<ShoppingList>()
            .eq(ShoppingList::getUserId, userId).eq(ShoppingList::getSupplierId, id).eq(ShoppingList::getStatus, "BOUGHT");
        if (startDate != null && !startDate.isBlank()) w.ge(ShoppingList::getBoughtTime, startDate + " 00:00:00");
        if (endDate != null && !endDate.isBlank()) w.le(ShoppingList::getBoughtTime, endDate + " 23:59:59");
        List<ShoppingList> list = slMapper.selectList(w);
        Map<String, Object> r = new HashMap<>();
        r.put("supplierId", id);
        r.put("purchaseCount", list.size());
        long totalQty = 0; BigDecimal totalAmt = BigDecimal.ZERO;
        for (ShoppingList sl : list) {
            int qty = sl.getActualQty() != null ? sl.getActualQty() : sl.getPlannedQty();
            totalQty += qty;
            if (sl.getPrice() != null) totalAmt = totalAmt.add(sl.getPrice().multiply(BigDecimal.valueOf(qty)));
        }
        r.put("totalQuantity", totalQty);
        r.put("estimatedAmount", totalAmt);
        return r;
    }

    private Supplier findOwn(Long userId, Long id) {
        Supplier s = mapper.selectById(id);
        if (s == null || !s.getUserId().equals(userId)) throw BusinessException.notFound("供应商不存在");
        return s;
    }
}
