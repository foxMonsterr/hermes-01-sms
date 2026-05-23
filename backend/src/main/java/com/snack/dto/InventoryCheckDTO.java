package com.snack.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InventoryCheckDTO {
    @NotNull @Min(0)
    private Integer actualQty;
    private String remark;

    public Integer getActualQty() { return actualQty; }
    public void setActualQty(Integer v) { actualQty = v; }
    public String getRemark() { return remark; }
    public void setRemark(String v) { remark = v; }
}
