package com.snack.controller;

import com.snack.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@Tag(name = "数据导出", description = "Excel 导出（P2 选做）")
@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @Operation(summary = "导出零食清单", description = "支持与零食列表相同的筛选条件")
    @GetMapping("/snacks")
    public void exportSnacks(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Long categoryId,
                              @RequestParam(required = false) String expiryStatus,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        exportService.exportSnacks(currentUserId, keyword, categoryId, expiryStatus, response);
    }

    @Operation(summary = "导出库存流水", description = "支持与流水列表相同的筛选条件")
    @GetMapping("/stock-records")
    public void exportStockRecords(@RequestParam(required = false) Long snackId,
                                    @RequestParam(required = false) String changeType,
                                    @RequestParam(required = false) String startDate,
                                    @RequestParam(required = false) String endDate,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        exportService.exportStockRecords(currentUserId, snackId, changeType, startDate, endDate, response);
    }
}
