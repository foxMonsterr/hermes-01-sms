package com.snack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.snack.common.BusinessException;
import com.snack.common.PageData;
import com.snack.dto.StockRecordQueryDTO;
import com.snack.entity.StockRecord;
import com.snack.mapper.StockRecordMapper;
import com.snack.service.StockRecordService;
import com.snack.vo.StockRecordStatsVO;
import com.snack.vo.StockRecordVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StockRecordServiceImpl implements StockRecordService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final StockRecordMapper stockRecordMapper;

    public StockRecordServiceImpl(StockRecordMapper stockRecordMapper) {
        this.stockRecordMapper = stockRecordMapper;
    }

    @Override
    public PageData<StockRecordVO> page(Long currentUserId, StockRecordQueryDTO query) {
        Page<StockRecord> mpPage = new Page<>(query.getPage(), query.getSize());

        LambdaQueryWrapper<StockRecord> wrapper = new LambdaQueryWrapper<StockRecord>()
                .eq(StockRecord::getUserId, currentUserId);

        applyFilters(wrapper, query);
        wrapper.orderByDesc(StockRecord::getCreateTime);

        Page<StockRecord> result = stockRecordMapper.selectPage(mpPage, wrapper);
        return toPageData(result);
    }

    @Override
    public PageData<StockRecordVO> pageBySnack(Long currentUserId, Long snackId, StockRecordQueryDTO query) {
        Page<StockRecord> mpPage = new Page<>(query.getPage(), query.getSize());

        LambdaQueryWrapper<StockRecord> wrapper = new LambdaQueryWrapper<StockRecord>()
                .eq(StockRecord::getUserId, currentUserId)
                .eq(StockRecord::getSnackId, snackId);

        applyFilters(wrapper, query);
        wrapper.orderByDesc(StockRecord::getCreateTime);

        Page<StockRecord> result = stockRecordMapper.selectPage(mpPage, wrapper);
        return toPageData(result);
    }

    @Override
    public StockRecordStatsVO stats(Long currentUserId) {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(LocalTime.MAX);

        // 全部历史 IN
        Long totalIn = sumChangeQty(currentUserId, "IN", null, null);
        // 全部历史 OUT（取绝对值）
        Long totalOut = sumChangeQty(currentUserId, "OUT", null, null);
        // 今日 IN
        Long todayIn = sumChangeQty(currentUserId, "IN", todayStart, todayEnd);
        // 今日 OUT
        Long todayOut = sumChangeQty(currentUserId, "OUT", todayStart, todayEnd);

        // 最近 5 条流水
        List<StockRecord> recentList = stockRecordMapper.selectList(
                new LambdaQueryWrapper<StockRecord>()
                        .eq(StockRecord::getUserId, currentUserId)
                        .orderByDesc(StockRecord::getCreateTime)
                        .last("LIMIT 5")
        );

        StockRecordStatsVO vo = new StockRecordStatsVO();
        vo.setTotalIn(totalIn);
        vo.setTotalOut(totalOut);
        vo.setTodayIn(todayIn);
        vo.setTodayOut(todayOut);
        vo.setRecentRecords(recentList.stream().map(this::toVO).toList());
        return vo;
    }

    // ---------- helpers ----------

    private void applyFilters(LambdaQueryWrapper<StockRecord> wrapper, StockRecordQueryDTO query) {
        if (query.getSnackId() != null) {
            wrapper.eq(StockRecord::getSnackId, query.getSnackId());
        }
        if (query.getChangeType() != null && !query.getChangeType().isBlank()) {
            // 校验枚举值
            String ct = query.getChangeType().toUpperCase();
            if (!ct.matches("INIT|IN|OUT|ADJUST")) {
                throw BusinessException.badRequest("changeType 参数不合法，合法值: INIT/IN/OUT/ADJUST");
            }
            wrapper.eq(StockRecord::getChangeType, ct);
        }
        if (query.getStartDate() != null && !query.getStartDate().isBlank()) {
            LocalDateTime start = LocalDate.parse(query.getStartDate(), DATE_FMT).atStartOfDay();
            wrapper.ge(StockRecord::getCreateTime, start);
        }
        if (query.getEndDate() != null && !query.getEndDate().isBlank()) {
            if (query.getStartDate() != null && query.getStartDate().compareTo(query.getEndDate()) > 0) {
                throw BusinessException.badRequest("开始日期不能晚于结束日期");
            }
            LocalDateTime end = LocalDate.parse(query.getEndDate(), DATE_FMT).atTime(LocalTime.MAX);
            wrapper.le(StockRecord::getCreateTime, end);
        }
    }

    private long sumChangeQty(Long userId, String changeType, LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<StockRecord> wrapper = new LambdaQueryWrapper<StockRecord>()
                .eq(StockRecord::getUserId, userId)
                .eq(StockRecord::getChangeType, changeType);
        if (start != null) wrapper.ge(StockRecord::getCreateTime, start);
        if (end != null) wrapper.le(StockRecord::getCreateTime, end);

        List<StockRecord> records = stockRecordMapper.selectList(wrapper);
        return records.stream().mapToLong(r -> Math.abs(r.getChangeQty())).sum();
    }

    private PageData<StockRecordVO> toPageData(Page<StockRecord> result) {
        List<StockRecordVO> records = result.getRecords().stream()
                .map(this::toVO).toList();
        PageData<StockRecordVO> pageData = new PageData<>();
        pageData.setRecords(records);
        pageData.setTotal(result.getTotal());
        pageData.setPage(result.getCurrent());
        pageData.setSize(result.getSize());
        return pageData;
    }

    private StockRecordVO toVO(StockRecord r) {
        StockRecordVO vo = new StockRecordVO();
        vo.setId(r.getId());
        vo.setSnackId(r.getSnackId());
        vo.setSnackName(r.getSnackName());
        vo.setChangeType(r.getChangeType());
        vo.setChangeQty(r.getChangeQty());
        vo.setBeforeQty(r.getBeforeQty());
        vo.setAfterQty(r.getAfterQty());
        vo.setRemark(r.getRemark());
        vo.setCreateTime(r.getCreateTime());
        return vo;
    }
}
