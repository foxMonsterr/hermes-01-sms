package com.snack.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("shop_review")
public class ShopReview {
    @TableId(type=IdType.AUTO)
    private Long id;
    private Long snackId;
    private Long orderId;
    private Long userId;
    private String username;
    private String content;
    private Integer isHidden;
    private LocalDateTime createTime;
    @TableLogic
    private Integer isDeleted;
}
