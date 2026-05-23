package com.snack.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 库存流水 — 审计记录，用户不可新增/修改/删除
 */
@Data
@TableName("stock_record")
public class StockRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long snackId;

    /** 零食名称快照 */
    private String snackName;

    /** INIT / IN / OUT / ADJUST */
    private String changeType;

    /** 入库为正，出库为负 */
    private Integer changeQty;

    private Integer beforeQty;

    private Integer afterQty;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
