package com.snack.vo;
import java.math.BigDecimal;

public class ShopProductVO {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private Integer quantity;
    private String unit;
    private BigDecimal price;
    private String imageUrl;
    private String description;
    private String expiryStatus;
    private Boolean inStock;
    public ShopProductVO(){}
    public ShopProductVO(Long id,String name,Long categoryId,String categoryName,Integer quantity,String unit,BigDecimal price,String imageUrl,String description,String expiryStatus,Boolean inStock){this.id=id;this.name=name;this.categoryId=categoryId;this.categoryName=categoryName;this.quantity=quantity;this.unit=unit;this.price=price;this.imageUrl=imageUrl;this.description=description;this.expiryStatus=expiryStatus;this.inStock=inStock;}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public Long getCategoryId(){return categoryId;} public void setCategoryId(Long v){categoryId=v;}
    public String getCategoryName(){return categoryName;} public void setCategoryName(String v){categoryName=v;}
    public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;}
    public String getUnit(){return unit;} public void setUnit(String v){unit=v;}
    public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;}
    public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getExpiryStatus(){return expiryStatus;} public void setExpiryStatus(String v){expiryStatus=v;}
    public Boolean getInStock(){return inStock;} public void setInStock(Boolean v){inStock=v;}
}
