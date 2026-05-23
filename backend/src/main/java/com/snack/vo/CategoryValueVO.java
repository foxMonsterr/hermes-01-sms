package com.snack.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "分类价值")
public class CategoryValueVO {

    @Schema(description = "分类名称", example = "膨化食品")
    private String categoryName;

    @Schema(description = "分类库存总价值", example = "120.00")
    private BigDecimal totalValue;

    @Schema(description = "占比", example = "31.1")
    private BigDecimal percentage;

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    public BigDecimal getPercentage() { return percentage; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
}
