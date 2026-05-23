package com.snack.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "出入库统计")
public class StockRecordStatsVO {

    @Schema(description = "总入库数量（仅IN类型）", example = "150")
    private Long totalIn;

    @Schema(description = "总出库数量（仅OUT类型）", example = "80")
    private Long totalOut;

    @Schema(description = "今日入库数量", example = "5")
    private Long todayIn;

    @Schema(description = "今日出库数量", example = "3")
    private Long todayOut;

    @Schema(description = "最近流水记录")
    private List<StockRecordVO> recentRecords;

    public Long getTotalIn() { return totalIn; }
    public void setTotalIn(Long totalIn) { this.totalIn = totalIn; }
    public Long getTotalOut() { return totalOut; }
    public void setTotalOut(Long totalOut) { this.totalOut = totalOut; }
    public Long getTodayIn() { return todayIn; }
    public void setTodayIn(Long todayIn) { this.todayIn = todayIn; }
    public Long getTodayOut() { return todayOut; }
    public void setTodayOut(Long todayOut) { this.todayOut = todayOut; }
    public List<StockRecordVO> getRecentRecords() { return recentRecords; }
    public void setRecentRecords(List<StockRecordVO> recentRecords) { this.recentRecords = recentRecords; }
}
