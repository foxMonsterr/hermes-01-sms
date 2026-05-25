package com.snack.service;
import com.snack.dto.ShopOrderCreateDTO;
import com.snack.vo.ShopOrderVO;
import java.util.List;

public interface ShopOrderService {
    ShopOrderVO createOrder(Long userId, Long ownerUserId, ShopOrderCreateDTO dto);
    List<ShopOrderVO> getMyOrders(Long userId, int page, int size, String status);
    long countMyOrders(Long userId, String status);
    ShopOrderVO getOrderDetail(Long userId, Long orderId);
    void cancelOrder(Long userId, Long orderId, Long ownerUserId);
    void confirmReceive(Long userId, Long orderId);
}
