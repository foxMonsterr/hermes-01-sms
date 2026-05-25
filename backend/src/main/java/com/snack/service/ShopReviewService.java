package com.snack.service;
import com.snack.vo.ShopReviewVO;
import java.util.List;

public interface ShopReviewService {
    List<ShopReviewVO> listBySnackId(Long snackId);
    ShopReviewVO create(Long userId, String username, Long snackId, Long orderId, String content);
    void delete(Long userId, Long reviewId);
}
