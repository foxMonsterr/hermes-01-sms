package com.snack.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "每日出入库数据")
public class DailyDataVO {

    @Schema(description = "日期", example = "2026-05-17")
    private String date;

    @Schema(description = "当日入库数量", example = "5")
    private Long inQty;

    @Schema(description = "当日出库数量", example = "3")
    private Long outQty;

    public DailyDataVO() {}

    public DailyDataVO(String date, Long inQty, Long outQty) {
        this.date = date;
        this.inQty = inQty;
        this.outQty = outQty;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public Long getInQty() { return inQty; }
    public void setInQty(Long inQty) { this.inQty = inQty; }
    public Long getOutQty() { return outQty; }
    public void setOutQty(Long outQty) { this.outQty = outQty; }
}
