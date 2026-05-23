package com.snack.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("shopping_list")
public class ShoppingList {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long snackId;

    private String snackName;

    private Long categoryId;

    private Integer plannedQty;

    private BigDecimal price;

    /** PENDING / BOUGHT / CANCELLED */
    private String status;

    /** MANUAL / LOW_STOCK */
    private String source;

    /** v5.0 供应商ID */
    private Long supplierId;

    /** v5.0 供应商名称快照 */
    private String supplierName;

    /** v5.0 实际入库数量 */
    private Integer actualQty;

    /** v5.0 入库时间 */
    private java.time.LocalDateTime boughtTime;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
