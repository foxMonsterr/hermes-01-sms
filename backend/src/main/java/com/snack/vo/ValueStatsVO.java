package com.snack.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "库存价值统计")
public class ValueStatsVO {

    @Schema(description = "库存总价值", example = "386.50")
    private BigDecimal totalValue;

    @Schema(description = "各分类价值分布")
    private List<CategoryValueVO> categoryValues;

    @Schema(description = "最贵零食")
    private List<PriciestSnackVO> priciestSnacks;

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    public List<CategoryValueVO> getCategoryValues() { return categoryValues; }
    public void setCategoryValues(List<CategoryValueVO> categoryValues) { this.categoryValues = categoryValues; }
    public List<PriciestSnackVO> getPriciestSnacks() { return priciestSnacks; }
    public void setPriciestSnacks(List<PriciestSnackVO> priciestSnacks) { this.priciestSnacks = priciestSnacks; }
}
