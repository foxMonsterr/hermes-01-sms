package com.snack.vo;
import java.math.BigDecimal;

public class ShopCartItemVO {
    private Long id;
    private Long snackId;
    private String snackName;
    private String imageUrl;
    private BigDecimal price;
    private Integer quantity;
    private Integer stock;
    private Boolean onShelf;
    private BigDecimal subtotal;
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public Long getSnackId(){return snackId;} public void setSnackId(Long v){snackId=v;}
    public String getSnackName(){return snackName;} public void setSnackName(String v){snackName=v;}
    public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;}
    public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;}
    public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;}
    public Integer getStock(){return stock;} public void setStock(Integer v){stock=v;}
    public Boolean getOnShelf(){return onShelf;} public void setOnShelf(Boolean v){onShelf=v;}
    public BigDecimal getSubtotal(){return subtotal;} public void setSubtotal(BigDecimal v){subtotal=v;}
}
