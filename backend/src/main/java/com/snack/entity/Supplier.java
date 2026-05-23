package com.snack.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("supplier")
public class Supplier {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private String contact;
    private String phone;
    private String address;
    private String notes;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
}
