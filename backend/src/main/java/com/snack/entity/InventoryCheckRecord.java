package com.snack.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("inventory_check_record")
public class InventoryCheckRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long snackId;
    private String snackName;
    private Integer systemQty;
    private Integer actualQty;
    private Integer difference;
    private String remark;
    private LocalDate checkDate;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
