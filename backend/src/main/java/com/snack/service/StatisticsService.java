package com.snack.service;

import com.snack.vo.CategoryDistributionVO;
import com.snack.vo.StatisticsOverviewVO;
import com.snack.vo.StockTrendVO;
import com.snack.vo.ValueStatsVO;

import java.util.List;

/**
 * 统计服务（用户隔离）v4.0
 */
public interface StatisticsService {

    StatisticsOverviewVO overview(Long currentUserId);

    List<CategoryDistributionVO> categoryDistribution(Long currentUserId);

    /** v4.0: 出入库趋势 */
    StockTrendVO stockTrend(Long currentUserId, String period);

    /** v4.0: 库存价值统计 */
    ValueStatsVO valueStats(Long currentUserId);

    /** v5.0: 消耗分析 */
    java.util.Map<String, Object> consumptionAnalysis(Long currentUserId, String period);
}
