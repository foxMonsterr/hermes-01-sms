package com.snack.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 分类分布
 */
@Schema(description = "分类分布")
public class CategoryDistributionVO {

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "分类名称", example = "膨化食品")
    private String categoryName;

    @Schema(description = "零食种类数", example = "6")
    private Integer snackCount;

    @Schema(description = "零食总数量", example = "15")
    private Integer totalQuantity;

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public Integer getSnackCount() { return snackCount; }
    public void setSnackCount(Integer snackCount) { this.snackCount = snackCount; }
    public Integer getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Integer totalQuantity) { this.totalQuantity = totalQuantity; }
}
