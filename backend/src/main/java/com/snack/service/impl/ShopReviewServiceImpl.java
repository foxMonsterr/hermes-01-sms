package com.snack.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.snack.common.BusinessException;
import com.snack.entity.ShopOrderItem;
import com.snack.entity.ShopReview;
import com.snack.mapper.ShopOrderItemMapper;
import com.snack.mapper.ShopReviewMapper;
import com.snack.service.ShopReviewService;
import com.snack.vo.ShopReviewVO;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShopReviewServiceImpl implements ShopReviewService {
    private final ShopReviewMapper reviewMapper;
    private final ShopOrderItemMapper orderItemMapper;

    public ShopReviewServiceImpl(ShopReviewMapper reviewMapper, ShopOrderItemMapper orderItemMapper) {
        this.reviewMapper = reviewMapper; this.orderItemMapper = orderItemMapper;
    }

    @Override
    public List<ShopReviewVO> listBySnackId(Long snackId) {
        return reviewMapper.selectList(new LambdaQueryWrapper<ShopReview>()
            .eq(ShopReview::getSnackId, snackId).eq(ShopReview::getIsHidden, 0)
            .orderByDesc(ShopReview::getCreateTime))
            .stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public ShopReviewVO create(Long userId, String username, Long snackId, Long orderId, String content) {
        // 校验该订单下确实买了这个商品
        long count = orderItemMapper.selectCount(new LambdaQueryWrapper<ShopOrderItem>()
            .eq(ShopOrderItem::getOrderId, orderId)
            .eq(ShopOrderItem::getSnackId, snackId));
        if (count == 0) throw BusinessException.badRequest("该订单中不包含此商品");

        // 防重复
        boolean exists = reviewMapper.exists(new LambdaQueryWrapper<ShopReview>()
            .eq(ShopReview::getUserId, userId).eq(ShopReview::getOrderId, orderId).eq(ShopReview::getSnackId, snackId));
        if (exists) throw BusinessException.badRequest("已评价过该商品");

    ShopReview r = new ShopReview();
    r.setSnackId(snackId); r.setOrderId(orderId); r.setUserId(userId);
    r.setUsername(username); r.setContent(content);
    r.setIsDeleted(0);
    reviewMapper.insert(r);
        return toVO(r);
    }

    @Override
    public void delete(Long userId, Long reviewId) {
        ShopReview r = reviewMapper.selectById(reviewId);
        if (r==null||!r.getUserId().equals(userId)) throw BusinessException.badRequest("无权删除");
        reviewMapper.deleteById(reviewId);
    }

    private ShopReviewVO toVO(ShopReview r) {
        ShopReviewVO vo = new ShopReviewVO();
        vo.setId(r.getId()); vo.setUsername(r.getUsername()); vo.setContent(r.getContent());
        vo.setCreateTime(r.getCreateTime());
        return vo;
    }
}
