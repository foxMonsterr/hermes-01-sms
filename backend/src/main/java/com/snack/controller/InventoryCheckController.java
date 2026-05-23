package com.snack.controller;

import com.snack.common.PageData;
import com.snack.common.Result;
import com.snack.dto.InventoryCheckDTO;
import com.snack.entity.InventoryCheckRecord;
import com.snack.service.InventoryCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Tag(name = "库存盘点", description = "库存盘点记录与统计")
@RestController
public class InventoryCheckController {
    private final InventoryCheckService service;
    public InventoryCheckController(InventoryCheckService s) { this.service = s; }

    @Operation(summary = "分页查询盘点记录")
    @GetMapping("/api/inventory-checks")
    public Result<PageData<InventoryCheckRecord>> page(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate, HttpServletRequest req) {
        Long uid = (Long) req.getAttribute("currentUserId");
        return Result.success(service.page(uid, page, size, startDate, endDate));
    }

    @Operation(summary = "正式盘点")
    @PostMapping("/api/snacks/{id}/inventory-check")
    public Result<Map<String, Object>> check(@PathVariable Long id, @Valid @RequestBody InventoryCheckDTO dto,
                                              HttpServletRequest req) {
        Long uid = (Long) req.getAttribute("currentUserId");
        return Result.success(service.check(uid, id, dto));
    }

    @Operation(summary = "盘点差异统计")
    @GetMapping("/api/inventory-checks/stats")
    public Result<Map<String, Object>> stats(@RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate, HttpServletRequest req) {
        Long uid = (Long) req.getAttribute("currentUserId");
        return Result.success(service.stats(uid, startDate, endDate));
    }
}
