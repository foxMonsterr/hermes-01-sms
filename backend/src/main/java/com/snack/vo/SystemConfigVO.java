package com.snack.vo;

public class SystemConfigVO {
    private Integer lowStockThreshold = 2;
    private Integer expiryAlertDays = 30;
    private Integer defaultPageSize = 10;
    private Boolean autoGenerateNotification = true;

    public Integer getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(Integer v) { this.lowStockThreshold = v; }
    public Integer getExpiryAlertDays() { return expiryAlertDays; }
    public void setExpiryAlertDays(Integer v) { this.expiryAlertDays = v; }
    public Integer getDefaultPageSize() { return defaultPageSize; }
    public void setDefaultPageSize(Integer v) { this.defaultPageSize = v; }
    public Boolean getAutoGenerateNotification() { return autoGenerateNotification; }
    public void setAutoGenerateNotification(Boolean v) { this.autoGenerateNotification = v; }
}
