package com.snack.controller;

import com.snack.common.PageData;
import com.snack.common.Result;
import com.snack.dto.DisposalDTO;
import com.snack.service.DisposalService;
import com.snack.vo.DisposalRecordVO;
import com.snack.vo.DisposalStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Tag(name = "过期处理", description = "过期/丢弃处理记录与统计")
@RestController
public class DisposalController {
    private final DisposalService service;
    public DisposalController(DisposalService s) { this.service = s; }

    @Operation(summary = "分页查询处理记录")
    @GetMapping("/api/disposals")
    public Result<PageData<DisposalRecordVO>> page(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate, HttpServletRequest req) {
        Long uid = (Long) req.getAttribute("currentUserId");
        return Result.success(service.page(uid, page, size, startDate, endDate));
    }

    @Operation(summary = "处理/丢弃零食")
    @PostMapping("/api/snacks/{id}/dispose")
    public Result<Map<String, Object>> dispose(@PathVariable Long id, @Valid @RequestBody DisposalDTO dto,
                                                HttpServletRequest req) {
        Long uid = (Long) req.getAttribute("currentUserId");
        return Result.success(service.dispose(uid, id, dto));
    }

    @Operation(summary = "损耗统计")
    @GetMapping("/api/disposals/stats")
    public Result<DisposalStatsVO> stats(@RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate, HttpServletRequest req) {
        Long uid = (Long) req.getAttribute("currentUserId");
        return Result.success(service.stats(uid, startDate, endDate));
    }
}
