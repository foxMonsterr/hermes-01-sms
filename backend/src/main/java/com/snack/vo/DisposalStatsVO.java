package com.snack.vo;
import java.math.BigDecimal;

public class DisposalStatsVO {
    private Long totalDisposedQty;
    private Long expiredDisposedQty, damagedDisposedQty, manualDisposedQty;
    private BigDecimal estimatedLoss;

    public Long getTotalDisposedQty() { return totalDisposedQty; } public void setTotalDisposedQty(Long v) { totalDisposedQty = v; }
    public Long getExpiredDisposedQty() { return expiredDisposedQty; } public void setExpiredDisposedQty(Long v) { expiredDisposedQty = v; }
    public Long getDamagedDisposedQty() { return damagedDisposedQty; } public void setDamagedDisposedQty(Long v) { damagedDisposedQty = v; }
    public Long getManualDisposedQty() { return manualDisposedQty; } public void setManualDisposedQty(Long v) { manualDisposedQty = v; }
    public BigDecimal getEstimatedLoss() { return estimatedLoss; } public void setEstimatedLoss(BigDecimal v) { estimatedLoss = v; }
}
