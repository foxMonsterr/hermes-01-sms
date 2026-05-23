package com.snack.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类实体 — snack_category（用户隔离）
 */
@Data
@TableName("snack_category")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属用户 */
    private Long userId;

    private String name;
    private String icon;
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
