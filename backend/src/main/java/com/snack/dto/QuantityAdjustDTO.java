package com.snack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "盘点调整库存请求")
public class QuantityAdjustDTO {

    @NotNull
    @Min(0)
    @Schema(description = "调整后的库存数量", example = "10")
    private Integer quantity;

    @Schema(description = "调整备注", example = "盘点调整")
    private String remark;

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
