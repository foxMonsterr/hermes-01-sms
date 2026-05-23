package com.snack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 零食更新请求
 * v4.0: 不包含 quantity，库存只能通过专用接口修改
 */
@Schema(description = "零食更新请求")
public class SnackUpdateDTO {

    @NotBlank
    @Size(max = 100)
    @Schema(description = "零食名称", example = "乐事薯片")
    private String name;

    @NotNull
    @Schema(description = "所属分类ID", example = "1")
    private Long categoryId;

    @Size(max = 20)
    @Schema(description = "单位", example = "包")
    private String unit;

    @Schema(description = "单价", example = "5.50")
    @jakarta.validation.constraints.DecimalMin("0")
    private BigDecimal price;

    @Schema(description = "采购日期", example = "2026-05-01")
    private LocalDate purchaseDate;

    @Schema(description = "过期日期", example = "2026-12-01")
    private LocalDate expiryDate;

    @Size(max = 500)
    @Schema(description = "备注", example = "黄瓜味")
    private String notes;

    // getters & setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
