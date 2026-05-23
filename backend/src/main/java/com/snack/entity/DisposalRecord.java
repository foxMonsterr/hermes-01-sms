package com.snack.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("disposal_record")
public class DisposalRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long snackId;
    private String snackName;
    private Integer quantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal totalLoss;
    private String reason;
    private String remark;
    private LocalDate disposeDate;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
