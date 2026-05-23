package com.snack.controller;

import com.snack.common.PageData;
import com.snack.common.Result;
import com.snack.entity.OperationLog;
import com.snack.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@Tag(name = "操作日志", description = "关键操作审计记录")
@RestController
@RequestMapping("/api/logs")
public class OperationLogController {
    private final OperationLogService service;
    public OperationLogController(OperationLogService s) { this.service = s; }

    @Operation(summary = "分页查询操作日志")
    @GetMapping
    public Result<PageData<OperationLog>> page(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletRequest req) {
        Long uid = (Long) req.getAttribute("currentUserId");
        return Result.success(service.page(uid, page, size, action, targetType, startDate, endDate));
    }
}
