package com.snack.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "未读提醒数量")
public class UnreadCountVO {

    @Schema(description = "未读总数")
    private Long count;

    @Schema(description = "即将过期未读数")
    private Long expirySoonCount;

    @Schema(description = "已过期未读数")
    private Long expiredCount;

    @Schema(description = "低库存未读数")
    private Long lowStockCount;

    @Schema(description = "缺货未读数")
    private Long stockOutCount;

    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }
    public Long getExpirySoonCount() { return expirySoonCount; }
    public void setExpirySoonCount(Long expirySoonCount) { this.expirySoonCount = expirySoonCount; }
    public Long getExpiredCount() { return expiredCount; }
    public void setExpiredCount(Long expiredCount) { this.expiredCount = expiredCount; }
    public Long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(Long lowStockCount) { this.lowStockCount = lowStockCount; }
    public Long getStockOutCount() { return stockOutCount; }
    public void setStockOutCount(Long stockOutCount) { this.stockOutCount = stockOutCount; }
}
