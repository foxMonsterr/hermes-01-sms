package com.snack.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "消耗排行")
public class TopConsumedVO {

    @Schema(description = "零食名称", example = "乐事薯片")
    private String snackName;

    @Schema(description = "总消耗数量", example = "8")
    private Long totalOut;

    public String getSnackName() { return snackName; }
    public void setSnackName(String snackName) { this.snackName = snackName; }
    public Long getTotalOut() { return totalOut; }
    public void setTotalOut(Long totalOut) { this.totalOut = totalOut; }
}
