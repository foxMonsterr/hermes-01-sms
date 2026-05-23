package com.snack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.snack.common.BusinessException;
import com.snack.common.PageData;
import com.snack.dto.DisposalDTO;
import com.snack.entity.*;
import com.snack.mapper.*;
import com.snack.service.DisposalService;
import com.snack.vo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class DisposalServiceImpl implements DisposalService {
    private final DisposalRecordMapper disposalMapper;
    private final SnackMapper snackMapper;
    private final StockRecordMapper stockRecordMapper;

    public DisposalServiceImpl(DisposalRecordMapper d, SnackMapper s, StockRecordMapper sr) {
        this.disposalMapper = d; this.snackMapper = s; this.stockRecordMapper = sr;
    }

    @Override
    public PageData<DisposalRecordVO> page(Long userId, int page, int size, String startDate, String endDate) {
        LambdaQueryWrapper<DisposalRecord> w = new LambdaQueryWrapper<DisposalRecord>().eq(DisposalRecord::getUserId, userId);
        if (startDate != null && !startDate.isBlank()) w.ge(DisposalRecord::getDisposeDate, LocalDate.parse(startDate));
        if (endDate != null && !endDate.isBlank()) w.le(DisposalRecord::getDisposeDate, LocalDate.parse(endDate));
        w.orderByDesc(DisposalRecord::getCreateTime);
        Page<DisposalRecord> mp = disposalMapper.selectPage(new Page<>(page, size), w);
        List<DisposalRecordVO> records = mp.getRecords().stream().map(r -> {
            DisposalRecordVO vo = new DisposalRecordVO();
            vo.setId(r.getId()); vo.setSnackId(r.getSnackId()); vo.setSnackName(r.getSnackName());
            vo.setQuantity(r.getQuantity()); vo.setUnit(r.getUnit());
            vo.setUnitPrice(r.getUnitPrice()); vo.setTotalLoss(r.getTotalLoss());
            vo.setReason(r.getReason()); vo.setRemark(r.getRemark());
            vo.setDisposeDate(r.getDisposeDate()); vo.setCreateTime(r.getCreateTime());
            return vo;
        }).toList();
        PageData<DisposalRecordVO> pd = new PageData<>();
        pd.setRecords(records); pd.setTotal(mp.getTotal()); pd.setPage(mp.getCurrent()); pd.setSize(mp.getSize());
        return pd;
    }

    @Override
    @Transactional
    public Map<String, Object> dispose(Long userId, Long snackId, DisposalDTO dto) {
        Snack snack = snackMapper.selectById(snackId);
        if (snack == null || !snack.getUserId().equals(userId)) throw BusinessException.notFound("零食不存在");
        if (dto.getQuantity() > snack.getQuantity()) throw BusinessException.badRequest("处理数量不能超过当前库存");

        int oldQty = snack.getQuantity();
        int newQty = oldQty - dto.getQuantity();
        snack.setQuantity(newQty);
        snackMapper.updateById(snack);

        // 流水
        StockRecord sr = new StockRecord();
        sr.setUserId(userId); sr.setSnackId(snackId); sr.setSnackName(snack.getName());
        sr.setChangeType("OUT"); sr.setChangeQty(-dto.getQuantity());
        sr.setBeforeQty(oldQty); sr.setAfterQty(newQty);
        sr.setRemark("丢弃处理：" + reasonLabel(dto.getReason()) + (dto.getRemark() != null ? "，" + dto.getRemark() : ""));
        stockRecordMapper.insert(sr);

        // 丢弃记录
        DisposalRecord dr = new DisposalRecord();
        dr.setUserId(userId); dr.setSnackId(snackId); dr.setSnackName(snack.getName());
        dr.setQuantity(dto.getQuantity()); dr.setUnit(snack.getUnit());
        dr.setUnitPrice(snack.getPrice());
        dr.setTotalLoss(snack.getPrice() != null ? snack.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())) : null);
        dr.setReason(dto.getReason()); dr.setRemark(dto.getRemark());
        dr.setDisposeDate(LocalDate.now());
        disposalMapper.insert(dr);

        Map<String, Object> result = new HashMap<>();
        result.put("snackId", snackId); result.put("remainingQuantity", newQty);
        result.put("disposedQuantity", dto.getQuantity()); result.put("recordId", dr.getId());
        return result;
    }

    @Override
    public DisposalStatsVO stats(Long userId, String startDate, String endDate) {
        LambdaQueryWrapper<DisposalRecord> w = new LambdaQueryWrapper<DisposalRecord>().eq(DisposalRecord::getUserId, userId);
        if (startDate != null && !startDate.isBlank()) w.ge(DisposalRecord::getDisposeDate, LocalDate.parse(startDate));
        if (endDate != null && !endDate.isBlank()) w.le(DisposalRecord::getDisposeDate, LocalDate.parse(endDate));
        List<DisposalRecord> list = disposalMapper.selectList(w);
        DisposalStatsVO vo = new DisposalStatsVO();
        vo.setTotalDisposedQty(list.stream().mapToLong(DisposalRecord::getQuantity).sum());
        vo.setExpiredDisposedQty(list.stream().filter(r -> "EXPIRED".equals(r.getReason())).mapToLong(DisposalRecord::getQuantity).sum());
        vo.setDamagedDisposedQty(list.stream().filter(r -> "DAMAGED".equals(r.getReason())).mapToLong(DisposalRecord::getQuantity).sum());
        vo.setManualDisposedQty(list.stream().filter(r -> "MANUAL".equals(r.getReason())).mapToLong(DisposalRecord::getQuantity).sum());
        vo.setEstimatedLoss(list.stream().filter(r -> r.getTotalLoss() != null).map(DisposalRecord::getTotalLoss).reduce(BigDecimal.ZERO, BigDecimal::add));
        return vo;
    }

    private String reasonLabel(String r) { return "EXPIRED".equals(r) ? "已过期" : "DAMAGED".equals(r) ? "损坏" : "手动处理"; }
}
