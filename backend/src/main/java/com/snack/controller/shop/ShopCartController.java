package com.snack.controller.shop;
import com.snack.common.Result;
import com.snack.dto.ShopCartAddDTO;
import com.snack.service.ShopCartService;
import com.snack.vo.ShopCartItemVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shop/cart")
public class ShopCartController {
    private final ShopCartService shopCartService;
    private final Long ownerUserId;

    public ShopCartController(ShopCartService shopCartService,
            @Value("${shop.owner-user-id}") Long ownerUserId) {
        this.shopCartService = shopCartService; this.ownerUserId = ownerUserId;
    }

    @GetMapping
    public Result<List<ShopCartItemVO>> getCart(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("shopUserId");
        return Result.success(shopCartService.getCart(userId, ownerUserId));
    }

    @PostMapping
    public Result<ShopCartItemVO> add(@Valid @RequestBody ShopCartAddDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("shopUserId");
        return Result.success(shopCartService.addToCart(userId, ownerUserId, dto));
    }

    @PutMapping("/{id}")
    public Result<Void> updateQty(@PathVariable Long id, @RequestBody java.util.Map<String,Integer> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("shopUserId");
        shopCartService.updateQuantity(userId, id, body.get("quantity"), ownerUserId);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("shopUserId");
        shopCartService.deleteItem(userId, id);
        return Result.success("删除成功", null);
    }

    @DeleteMapping
    public Result<Void> clear(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("shopUserId");
        shopCartService.clearCart(userId);
        return Result.success("清空成功", null);
    }
}
