package com.snack.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "提醒查询请求")
public class NotificationQueryDTO {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer size = 10;

    @Schema(description = "提醒类型: EXPIRY_SOON/EXPIRED/LOW_STOCK/STOCK_OUT")
    private String type;

    @Schema(description = "是否已读: 0/1")
    private Integer isRead;

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getIsRead() { return isRead; }
    public void setIsRead(Integer isRead) { this.isRead = isRead; }
}
