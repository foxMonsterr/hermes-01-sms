package com.snack.service;
import com.snack.dto.ShopCartAddDTO;
import com.snack.vo.ShopCartItemVO;
import java.util.List;

public interface ShopCartService {
    List<ShopCartItemVO> getCart(Long userId, Long ownerUserId);
    ShopCartItemVO addToCart(Long userId, Long ownerUserId, ShopCartAddDTO dto);
    void updateQuantity(Long userId, Long cartId, Integer quantity, Long ownerUserId);
    void deleteItem(Long userId, Long cartId);
    void clearCart(Long userId);
}
