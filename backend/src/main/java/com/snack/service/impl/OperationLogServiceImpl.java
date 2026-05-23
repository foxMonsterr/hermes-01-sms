package com.snack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.snack.common.PageData;
import com.snack.entity.OperationLog;
import com.snack.mapper.OperationLogMapper;
import com.snack.service.OperationLogService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class OperationLogServiceImpl implements OperationLogService {
    private final OperationLogMapper mapper;
    public OperationLogServiceImpl(OperationLogMapper m) { this.mapper = m; }

    @Override
    public PageData<OperationLog> page(Long userId, int page, int size, String action, String targetType, String startDate, String endDate) {
        LambdaQueryWrapper<OperationLog> w = new LambdaQueryWrapper<OperationLog>().eq(OperationLog::getUserId, userId);
        if (action != null && !action.isBlank()) w.eq(OperationLog::getAction, action);
        if (targetType != null && !targetType.isBlank()) w.eq(OperationLog::getTargetType, targetType);
        if (startDate != null && !startDate.isBlank()) w.ge(OperationLog::getCreateTime, LocalDate.parse(startDate).atStartOfDay());
        if (endDate != null && !endDate.isBlank()) w.le(OperationLog::getCreateTime, LocalDate.parse(endDate).plusDays(1).atStartOfDay());
        w.orderByDesc(OperationLog::getCreateTime);
        Page<OperationLog> mp = mapper.selectPage(new Page<>(page, size), w);
        PageData<OperationLog> pd = new PageData<>();
        pd.setRecords(mp.getRecords()); pd.setTotal(mp.getTotal()); pd.setPage(mp.getCurrent()); pd.setSize(mp.getSize());
        return pd;
    }

    @Override
    public void log(Long userId, String username, String action, String targetType, Long targetId, String detail) {
        OperationLog log = new OperationLog();
        log.setUserId(userId); log.setUsername(username); log.setAction(action);
        log.setTargetType(targetType); log.setTargetId(targetId);
        log.setDetail(detail != null && detail.length() > 500 ? detail.substring(0, 500) : detail);
        mapper.insert(log);
    }
}
