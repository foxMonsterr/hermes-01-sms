package com.snack.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "最贵零食")
public class PriciestSnackVO {

    @Schema(description = "零食名称", example = "进口巧克力")
    private String snackName;

    @Schema(description = "总价", example = "89.00")
    private BigDecimal totalPrice;

    public String getSnackName() { return snackName; }
    public void setSnackName(String snackName) { this.snackName = snackName; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
}
