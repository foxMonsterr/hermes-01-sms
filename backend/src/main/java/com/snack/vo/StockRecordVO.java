package com.snack.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "库存流水记录")
public class StockRecordVO {

    @Schema(description = "流水ID", example = "1")
    private Long id;

    @Schema(description = "零食ID", example = "5")
    private Long snackId;

    @Schema(description = "零食名称", example = "乐事薯片")
    private String snackName;

    @Schema(description = "变动类型", example = "OUT")
    private String changeType;

    @Schema(description = "变动数量（正=入库，负=出库）", example = "-1")
    private Integer changeQty;

    @Schema(description = "变动前数量", example = "3")
    private Integer beforeQty;

    @Schema(description = "变动后数量", example = "2")
    private Integer afterQty;

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
    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }
    public Integer getChangeQty() { return changeQty; }
    public void setChangeQty(Integer changeQty) { this.changeQty = changeQty; }
    public Integer getBeforeQty() { return beforeQty; }
    public void setBeforeQty(Integer beforeQty) { this.beforeQty = beforeQty; }
    public Integer getAfterQty() { return afterQty; }
    public void setAfterQty(Integer afterQty) { this.afterQty = afterQty; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
