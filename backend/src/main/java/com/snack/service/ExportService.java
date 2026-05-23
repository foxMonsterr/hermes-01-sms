package com.snack.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ExportService {

    void exportSnacks(Long currentUserId, String keyword, Long categoryId, String expiryStatus,
                      HttpServletResponse response);

    void exportStockRecords(Long currentUserId, Long snackId, String changeType,
                            String startDate, String endDate, HttpServletResponse response);
}
