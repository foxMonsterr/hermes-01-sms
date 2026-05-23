package com.snack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "采购入库请求")
public class ShoppingListStockInDTO {

    @NotNull
    @Min(1)
    @Schema(description = "实际采购数量", example = "2")
    private Integer actualQty;

    @Schema(description = "实际单价", example = "5.50")
    private BigDecimal price;

    @Schema(description = "分类ID（新建零食时使用）")
    private Long categoryId;

    @Schema(description = "入库备注")
    private String remark;

    public Integer getActualQty() { return actualQty; }
    public void setActualQty(Integer actualQty) { this.actualQty = actualQty; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
