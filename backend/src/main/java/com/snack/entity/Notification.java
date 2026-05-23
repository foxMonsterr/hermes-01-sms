package com.snack.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("notification")
public class Notification {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** EXPIRY_SOON / EXPIRED / LOW_STOCK / STOCK_OUT */
    private String type;

    private String title;

    private String content;

    private Long relatedId;

    private LocalDate notifyDate;

    private Integer isRead;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
