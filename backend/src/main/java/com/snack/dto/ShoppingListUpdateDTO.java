package com.snack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "采购清单更新请求")
public class ShoppingListUpdateDTO {

    @NotBlank
    @Size(max = 100)
    @Schema(description = "零食名称")
    private String snackName;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Min(1)
    @Schema(description = "计划采购数量")
    private Integer plannedQty;

    @Schema(description = "预估单价")
    private BigDecimal price;

    @Schema(description = "备注")
    private String remark;

    public String getSnackName() { return snackName; }
    public void setSnackName(String snackName) { this.snackName = snackName; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Integer getPlannedQty() { return plannedQty; }
    public void setPlannedQty(Integer plannedQty) { this.plannedQty = plannedQty; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
