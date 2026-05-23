package com.snack.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DisposalDTO {
    @NotNull @Min(1)
    private Integer quantity;
    @NotBlank
    private String reason;
    private String remark;

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer v) { this.quantity = v; }
    public String getReason() { return reason; }
    public void setReason(String v) { this.reason = v; }
    public String getRemark() { return remark; }
    public void setRemark(String v) { this.remark = v; }
}
