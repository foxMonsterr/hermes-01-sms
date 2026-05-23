package com.snack.controller;

import com.snack.common.Result;
import com.snack.common.PageData;
import com.snack.dto.StockRecordQueryDTO;
import com.snack.service.StockRecordService;
import com.snack.vo.StockRecordStatsVO;
import com.snack.vo.StockRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@Tag(name = "库存流水", description = "库存变动流水查询与统计")
@RestController
@RequestMapping("/api/stock-records")
public class StockRecordController {

    private final StockRecordService stockRecordService;

    public StockRecordController(StockRecordService stockRecordService) {
        this.stockRecordService = stockRecordService;
    }

    @Operation(summary = "分页查询流水", description = "支持按零食/类型/日期筛选")
    @GetMapping
    public Result<PageData<StockRecordVO>> list(StockRecordQueryDTO query, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        return Result.success(stockRecordService.page(currentUserId, query));
    }

    @Operation(summary = "按零食查询流水", description = "查询指定零食的库存流水")
    @GetMapping("/snack/{id}")
    public Result<PageData<StockRecordVO>> bySnack(@PathVariable Long id, StockRecordQueryDTO query,
                                                    HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        return Result.success(stockRecordService.pageBySnack(currentUserId, id, query));
    }

    @Operation(summary = "出入库统计", description = "统计当前用户全部和今日出入库数量")
    @GetMapping("/stats")
    public Result<StockRecordStatsVO> stats(HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        return Result.success(stockRecordService.stats(currentUserId));
    }
}
