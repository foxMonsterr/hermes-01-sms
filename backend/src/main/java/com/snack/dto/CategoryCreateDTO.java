package com.snack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 分类创建请求
 */
@Schema(description = "分类创建请求")
public class CategoryCreateDTO {

    @NotBlank
    @Size(max = 50)
    @Schema(description = "分类名称", example = "进口零食")
    private String name;

    @Size(max = 50)
    @Schema(description = "图标", example = "🌍")
    private String icon;

    @Schema(description = "排序", example = "9")
    private Integer sortOrder;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
