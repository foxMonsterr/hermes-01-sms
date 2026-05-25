package com.snack.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("shop_order_item")
public class ShopOrderItem {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long snackId;
    private String snackName;
    private String imageUrl;
    private BigDecimal price;
    private Integer quantity;
    private String unit;
    private BigDecimal subtotal;
    private LocalDateTime createTime;
}
