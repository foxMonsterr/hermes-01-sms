package com.snack.controller;

import com.snack.common.Result;
import com.snack.service.StatisticsService;
import com.snack.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 统计接口 v4.0（用户隔离）
 */
@Tag(name = "统计看板", description = "零食库存统计与趋势分析")
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Operation(summary = "总览统计", description = "返回当前用户的零食库存总览")
    @GetMapping("/overview")
    public Result<StatisticsOverviewVO> overview(HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        return Result.success(statisticsService.overview(currentUserId));
    }

    @Operation(summary = "分类分布", description = "按分类统计零食数量，包含无零食的分类")
    @GetMapping("/category-distribution")
    public Result<List<CategoryDistributionVO>> categoryDistribution(HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        return Result.success(statisticsService.categoryDistribution(currentUserId));
    }

    @Operation(summary = "出入库趋势", description = "近7天/30天/90天出入库趋势和消耗排行")
    @GetMapping("/stock-trend")
    public Result<StockTrendVO> stockTrend(
            @Parameter(description = "统计周期: 7d/30d/90d，默认 7d")
            @RequestParam(defaultValue = "7d") String period,
            HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        return Result.success(statisticsService.stockTrend(currentUserId, period));
    }

    @Operation(summary = "库存价值统计", description = "库存总价值、分类价值分布、最贵零食")
    @GetMapping("/value")
    public Result<ValueStatsVO> value(HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        return Result.success(statisticsService.valueStats(currentUserId));
    }

    @Operation(summary = "消耗分析", description = "v5.0: 某周期消耗总量、日均、金额、Top排行")
    @GetMapping("/consumption-analysis")
    public Result<java.util.Map<String, Object>> consumptionAnalysis(
            @RequestParam(defaultValue = "30d") String period, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        return Result.success(statisticsService.consumptionAnalysis(currentUserId, period));
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("currentUserId");
    }
}
