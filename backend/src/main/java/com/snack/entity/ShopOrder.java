package com.snack.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("shop_order")
public class ShopOrder {
    @TableId(type=IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long shopUserId;
    private Long ownerUserId;
    private String status;
    private BigDecimal totalAmount;
    private Integer totalQuantity;
    private String receiver;
    private String phone;
    private String address;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime shipTime;
    private LocalDateTime completeTime;
    private LocalDateTime cancelTime;
    @TableLogic
    private Integer isDeleted;
}
