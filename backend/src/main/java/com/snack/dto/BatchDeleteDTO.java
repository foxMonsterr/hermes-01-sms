package com.snack.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 批量删除请求
 */
@Schema(description = "批量删除请求")
public class BatchDeleteDTO {

    @Schema(description = "要删除的零食 ID 列表", example = "[1, 2, 3]")
    private List<Long> ids;

    public List<Long> getIds() { return ids; }
    public void setIds(List<Long> ids) { this.ids = ids; }
}
