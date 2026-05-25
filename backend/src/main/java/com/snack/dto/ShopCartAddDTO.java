package com.snack.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ShopCartAddDTO {
    @NotNull private Long snackId;
    @Min(1) private Integer quantity = 1;
    public Long getSnackId(){return snackId;} public void setSnackId(Long v){snackId=v;}
    public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;}
}
