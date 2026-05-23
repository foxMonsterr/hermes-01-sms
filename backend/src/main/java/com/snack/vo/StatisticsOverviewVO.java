package com.snack.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 统计总览
 */
@Schema(description = "统计总览")
public class StatisticsOverviewVO {

    @Schema(description = "零食种类数", example = "20")
    private Long totalSnackCount;

    @Schema(description = "零食总数量", example = "56")
    private Long totalQuantity;

    @Schema(description = "已过期数量", example = "3")
    private Long expiredCount;

    @Schema(description = "即将过期数量（30天内）", example = "5")
    private Long soonExpiredCount;

    @Schema(description = "低库存数量（≤2）", example = "4")
    private Long lowStockCount;

    @Schema(description = "分类数量", example = "8")
    private Long categoryCount;

    @Schema(description = "库存总价值", example = "386.50")
    private java.math.BigDecimal totalValue;

    @Schema(description = "今日入库数量", example = "5")
    private Long todayInQty;

    @Schema(description = "今日出库数量", example = "3")
    private Long todayOutQty;

    public Long getTotalSnackCount() { return totalSnackCount; }
    public void setTotalSnackCount(Long totalSnackCount) { this.totalSnackCount = totalSnackCount; }
    public Long getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Long totalQuantity) { this.totalQuantity = totalQuantity; }
    public Long getExpiredCount() { return expiredCount; }
    public void setExpiredCount(Long expiredCount) { this.expiredCount = expiredCount; }
    public Long getSoonExpiredCount() { return soonExpiredCount; }
    public void setSoonExpiredCount(Long soonExpiredCount) { this.soonExpiredCount = soonExpiredCount; }
    public Long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(Long lowStockCount) { this.lowStockCount = lowStockCount; }
    public Long getCategoryCount() { return categoryCount; }
    public void setCategoryCount(Long categoryCount) { this.categoryCount = categoryCount; }
    public java.math.BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(java.math.BigDecimal totalValue) { this.totalValue = totalValue; }
    public Long getTodayInQty() { return todayInQty; }
    public void setTodayInQty(Long todayInQty) { this.todayInQty = todayInQty; }
    public Long getTodayOutQty() { return todayOutQty; }
    public void setTodayOutQty(Long todayOutQty) { this.todayOutQty = todayOutQty; }
}
