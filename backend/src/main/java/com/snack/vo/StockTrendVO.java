package com.snack.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "出入库趋势")
public class StockTrendVO {

    @Schema(description = "统计周期", example = "7d")
    private String period;

    @Schema(description = "周期内总入库", example = "40")
    private Long totalIn;

    @Schema(description = "周期内总出库", example = "25")
    private Long totalOut;

    @Schema(description = "每日出入库数据")
    private List<DailyDataVO> dailyData;

    @Schema(description = "消耗排行 Top 5")
    private List<TopConsumedVO> topConsumed;

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public Long getTotalIn() { return totalIn; }
    public void setTotalIn(Long totalIn) { this.totalIn = totalIn; }
    public Long getTotalOut() { return totalOut; }
    public void setTotalOut(Long totalOut) { this.totalOut = totalOut; }
    public List<DailyDataVO> getDailyData() { return dailyData; }
    public void setDailyData(List<DailyDataVO> dailyData) { this.dailyData = dailyData; }
    public List<TopConsumedVO> getTopConsumed() { return topConsumed; }
    public void setTopConsumed(List<TopConsumedVO> topConsumed) { this.topConsumed = topConsumed; }
}
