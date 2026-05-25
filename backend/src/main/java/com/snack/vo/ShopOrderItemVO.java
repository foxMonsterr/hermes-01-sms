package com.snack.vo;
import java.math.BigDecimal;

public class ShopOrderItemVO {
    private Long id;
    private Long snackId;
    private String snackName;
    private String imageUrl;
    private BigDecimal price;
    private Integer quantity;
    private String unit;
    private BigDecimal subtotal;
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public Long getSnackId(){return snackId;} public void setSnackId(Long v){snackId=v;}
    public String getSnackName(){return snackName;} public void setSnackName(String v){snackName=v;}
    public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;}
    public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;}
    public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;}
    public String getUnit(){return unit;} public void setUnit(String v){unit=v;}
    public BigDecimal getSubtotal(){return subtotal;} public void setSubtotal(BigDecimal v){subtotal=v;}
}
