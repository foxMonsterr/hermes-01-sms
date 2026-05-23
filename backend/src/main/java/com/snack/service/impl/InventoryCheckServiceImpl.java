package com.snack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.snack.common.BusinessException;
import com.snack.common.PageData;
import com.snack.dto.InventoryCheckDTO;
import com.snack.entity.*;
import com.snack.mapper.*;
import com.snack.service.InventoryCheckService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
public class InventoryCheckServiceImpl implements InventoryCheckService {
    private final InventoryCheckRecordMapper checkMapper;
    private final SnackMapper snackMapper;
    private final StockRecordMapper stockRecordMapper;

    public InventoryCheckServiceImpl(InventoryCheckRecordMapper c, SnackMapper s, StockRecordMapper sr) {
        this.checkMapper = c; this.snackMapper = s; this.stockRecordMapper = sr;
    }

    @Override
    public PageData<InventoryCheckRecord> page(Long userId, int page, int size, String startDate, String endDate) {
        LambdaQueryWrapper<InventoryCheckRecord> w = new LambdaQueryWrapper<InventoryCheckRecord>().eq(InventoryCheckRecord::getUserId, userId);
        if (startDate != null && !startDate.isBlank()) w.ge(InventoryCheckRecord::getCheckDate, LocalDate.parse(startDate));
        if (endDate != null && !endDate.isBlank()) w.le(InventoryCheckRecord::getCheckDate, LocalDate.parse(endDate));
        w.orderByDesc(InventoryCheckRecord::getCreateTime);
        Page<InventoryCheckRecord> mp = checkMapper.selectPage(new Page<>(page, size), w);
        PageData<InventoryCheckRecord> pd = new PageData<>();
        pd.setRecords(mp.getRecords()); pd.setTotal(mp.getTotal()); pd.setPage(mp.getCurrent()); pd.setSize(mp.getSize());
        return pd;
    }

    @Override
    @Transactional
    public Map<String, Object> check(Long userId, Long snackId, InventoryCheckDTO dto) {
        Snack snack = snackMapper.selectById(snackId);
        if (snack == null || !snack.getUserId().equals(userId)) throw BusinessException.notFound("零食不存在");

        int sysQty = snack.getQuantity();
        int actQty = dto.getActualQty();
        int diff = actQty - sysQty;

        if (diff != 0) {
            snack.setQuantity(actQty);
            snackMapper.updateById(snack);
            StockRecord sr = new StockRecord();
            sr.setUserId(userId); sr.setSnackId(snackId); sr.setSnackName(snack.getName());
            sr.setChangeType("ADJUST"); sr.setChangeQty(diff);
            sr.setBeforeQty(sysQty); sr.setAfterQty(actQty);
            sr.setRemark("正式盘点" + (dto.getRemark() != null ? "：" + dto.getRemark() : ""));
            stockRecordMapper.insert(sr);
        }

        InventoryCheckRecord cr = new InventoryCheckRecord();
        cr.setUserId(userId); cr.setSnackId(snackId); cr.setSnackName(snack.getName());
        cr.setSystemQty(sysQty); cr.setActualQty(actQty); cr.setDifference(diff);
        cr.setRemark(dto.getRemark()); cr.setCheckDate(LocalDate.now());
        checkMapper.insert(cr);

        Map<String, Object> r = new HashMap<>();
        r.put("snackId", snackId); r.put("systemQty", sysQty); r.put("actualQty", actQty);
        r.put("difference", diff); r.put("recordId", cr.getId());
        return r;
    }

    @Override
    public Map<String, Object> stats(Long userId, String startDate, String endDate) {
        LambdaQueryWrapper<InventoryCheckRecord> w = new LambdaQueryWrapper<InventoryCheckRecord>().eq(InventoryCheckRecord::getUserId, userId);
        if (startDate != null && !startDate.isBlank()) w.ge(InventoryCheckRecord::getCheckDate, LocalDate.parse(startDate));
        if (endDate != null && !endDate.isBlank()) w.le(InventoryCheckRecord::getCheckDate, LocalDate.parse(endDate));
        List<InventoryCheckRecord> list = checkMapper.selectList(w);
        Map<String, Object> r = new HashMap<>();
        r.put("totalCheckCount", list.size());
        r.put("differenceCount", list.stream().filter(c -> c.getDifference() != 0).count());
        r.put("totalPositiveDiff", list.stream().filter(c -> c.getDifference() > 0).mapToLong(InventoryCheckRecord::getDifference).sum());
        r.put("totalNegativeDiff", list.stream().filter(c -> c.getDifference() < 0).mapToLong(c -> Math.abs(c.getDifference())).sum());
        return r;
    }
}
