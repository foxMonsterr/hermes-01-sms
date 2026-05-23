package com.snack.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "采购清单项")
public class ShoppingListVO {

    @Schema(description = "清单ID")
    private Long id;

    @Schema(description = "关联零食ID")
    private Long snackId;

    @Schema(description = "零食名称")
    private String snackName;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "计划采购数量")
    private Integer plannedQty;

    @Schema(description = "预估单价")
    private BigDecimal price;

    @Schema(description = "状态: PENDING/BOUGHT/CANCELLED")
    private String status;

    @Schema(description = "来源: MANUAL/LOW_STOCK")
    private String source;

    @Schema(description = "供应商ID")
    private Long supplierId;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "实际入库数量")
    private Integer actualQty;

    @Schema(description = "入库时间")
    private java.time.LocalDateTime boughtTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSnackId() { return snackId; }
    public void setSnackId(Long snackId) { this.snackId = snackId; }
    public String getSnackName() { return snackName; }
    public void setSnackName(String snackName) { this.snackName = snackName; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public Integer getPlannedQty() { return plannedQty; }
    public void setPlannedQty(Integer plannedQty) { this.plannedQty = plannedQty; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long v) { this.supplierId = v; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String v) { this.supplierName = v; }
    public Integer getActualQty() { return actualQty; }
    public void setActualQty(Integer v) { this.actualQty = v; }
    public java.time.LocalDateTime getBoughtTime() { return boughtTime; }
    public void setBoughtTime(java.time.LocalDateTime v) { this.boughtTime = v; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
