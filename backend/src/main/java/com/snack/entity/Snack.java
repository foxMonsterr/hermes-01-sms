package com.snack.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 零食实体 — snack（用户隔离）
 */
@Data
@TableName("snack")
public class Snack {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属用户 */
    private Long userId;

    private String name;

    /** 所属分类 */
    private Long categoryId;

    private Integer quantity;
    private String unit;

    /** 单价（可选） */
    private java.math.BigDecimal price;

    /** 图片URL（P2选做） */
    private String imageUrl;

    private LocalDate purchaseDate;
    private LocalDate expiryDate;

    private String notes;

    /** v7.0 小卖铺扩展 */
    private Integer isOnShelf;
    private String description;
    private java.time.LocalDateTime shelfTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
