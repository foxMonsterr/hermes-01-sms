package com.snack.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 零食详情响应 — 不含 userId
 */
@Schema(description = "零食详情")
public class SnackVO {

    @Schema(description = "零食ID", example = "1")
    private Long id;

    @Schema(description = "名称", example = "乐事薯片")
    private String name;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "分类名称", example = "膨化食品")
    private String categoryName;

    @Schema(description = "数量", example = "3")
    private Integer quantity;

    @Schema(description = "单位", example = "包")
    private String unit;

    @Schema(description = "单价", example = "5.50")
    private java.math.BigDecimal price;

    @Schema(description = "总价（price × quantity）", example = "16.50")
    private java.math.BigDecimal totalPrice;

    @Schema(description = "图片URL（P2选做）", example = "https://example.com/img.jpg")
    private String imageUrl;

    @Schema(description = "采购日期", example = "2026-05-01")
    private LocalDate purchaseDate;

    @Schema(description = "过期日期", example = "2026-12-01")
    private LocalDate expiryDate;

    @Schema(description = "过期状态: expired / soon / fresh / unknown", example = "fresh")
    private String expiryStatus;

    @Schema(description = "距离过期天数，无过期日期返回 null", example = "223")
    private Long daysUntilExpiry;

    @Schema(description = "备注", example = "黄瓜味")
    private String notes;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public java.math.BigDecimal getPrice() { return price; }
    public void setPrice(java.math.BigDecimal price) { this.price = price; }
    public java.math.BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(java.math.BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public String getExpiryStatus() { return expiryStatus; }
    public void setExpiryStatus(String expiryStatus) { this.expiryStatus = expiryStatus; }
    public Long getDaysUntilExpiry() { return daysUntilExpiry; }
    public void setDaysUntilExpiry(Long daysUntilExpiry) { this.daysUntilExpiry = daysUntilExpiry; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
