package com.snack.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 零食查询请求（分页 + 筛选）
 */
@Schema(description = "零食查询请求")
public class SnackQueryDTO {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer size = 10;

    @Schema(description = "名称关键字", example = "薯片")
    private String keyword;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "过期状态: expired / soon / fresh", example = "soon")
    private String expiryStatus;

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getExpiryStatus() { return expiryStatus; }
    public void setExpiryStatus(String expiryStatus) { this.expiryStatus = expiryStatus; }
}
