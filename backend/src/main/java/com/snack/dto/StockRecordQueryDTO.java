package com.snack.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 库存流水查询请求
 */
@Schema(description = "库存流水查询")
public class StockRecordQueryDTO {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer size = 10;

    @Schema(description = "零食ID", example = "5")
    private Long snackId;

    @Schema(description = "变动类型: INIT/IN/OUT/ADJUST")
    private String changeType;

    @Schema(description = "开始日期", example = "2026-05-01")
    private String startDate;

    @Schema(description = "结束日期", example = "2026-05-23")
    private String endDate;

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public Long getSnackId() { return snackId; }
    public void setSnackId(Long snackId) { this.snackId = snackId; }
    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}
