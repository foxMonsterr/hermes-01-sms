package com.snack.service;
import com.snack.common.PageData;
import com.snack.entity.OperationLog;

public interface OperationLogService {
    PageData<OperationLog> page(Long userId, int page, int size, String action, String targetType, String startDate, String endDate);
    void log(Long userId, String username, String action, String targetType, Long targetId, String detail);
}
