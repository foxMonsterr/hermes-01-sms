package com.snack.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 分类响应
 */
@Schema(description = "分类信息")
public class CategoryVO {

    @Schema(description = "分类ID", example = "1")
    private Long id;

    @Schema(description = "分类名称", example = "膨化食品")
    private String name;

    @Schema(description = "图标", example = "🍿")
    private String icon;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "该分类下的零食数量", example = "6")
    private Long snackCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    public CategoryVO() {}
    public CategoryVO(Long id, String name, String icon, Integer sortOrder) {
        this.id = id; this.name = name; this.icon = icon; this.sortOrder = sortOrder;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Long getSnackCount() { return snackCount; }
    public void setSnackCount(Long snackCount) { this.snackCount = snackCount; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
