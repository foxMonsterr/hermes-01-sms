package com.snack.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("shop_cart")
public class ShopCart {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long userId;
    private Long snackId;
    private Integer quantity;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
