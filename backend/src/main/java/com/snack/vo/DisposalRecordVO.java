package com.snack.vo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DisposalRecordVO {
    private Long id; private Long snackId; private String snackName;
    private Integer quantity; private String unit;
    private BigDecimal unitPrice; private BigDecimal totalLoss;
    private String reason; private String remark;
    private LocalDate disposeDate; private LocalDateTime createTime;

    public Long getId() { return id; } public void setId(Long v) { id = v; }
    public Long getSnackId() { return snackId; } public void setSnackId(Long v) { snackId = v; }
    public String getSnackName() { return snackName; } public void setSnackName(String v) { snackName = v; }
    public Integer getQuantity() { return quantity; } public void setQuantity(Integer v) { quantity = v; }
    public String getUnit() { return unit; } public void setUnit(String v) { unit = v; }
    public BigDecimal getUnitPrice() { return unitPrice; } public void setUnitPrice(BigDecimal v) { unitPrice = v; }
    public BigDecimal getTotalLoss() { return totalLoss; } public void setTotalLoss(BigDecimal v) { totalLoss = v; }
    public String getReason() { return reason; } public void setReason(String v) { reason = v; }
    public String getRemark() { return remark; } public void setRemark(String v) { remark = v; }
    public LocalDate getDisposeDate() { return disposeDate; } public void setDisposeDate(LocalDate v) { disposeDate = v; }
    public LocalDateTime getCreateTime() { return createTime; } public void setCreateTime(LocalDateTime v) { createTime = v; }
}
