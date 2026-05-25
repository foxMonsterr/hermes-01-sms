package com.snack.controller.shop;
import com.snack.common.PageData;
import com.snack.common.Result;
import com.snack.dto.ShopOrderCreateDTO;
import com.snack.service.ShopOrderService;
import com.snack.vo.ShopOrderVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shop/orders")
public class ShopOrderController {
    private final ShopOrderService shopOrderService;
    private final Long ownerUserId;

    public ShopOrderController(ShopOrderService shopOrderService,
            @Value("${shop.owner-user-id}") Long ownerUserId) {
        this.shopOrderService = shopOrderService; this.ownerUserId = ownerUserId;
    }

    @PostMapping
    public Result<ShopOrderVO> create(@Valid @RequestBody ShopOrderCreateDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("shopUserId");
        return Result.success("下单成功", shopOrderService.createOrder(userId, ownerUserId, dto));
    }

    @GetMapping
    public Result<PageData<ShopOrderVO>> list(
            @RequestParam(defaultValue="1") int page,
            @RequestParam(defaultValue="10") int size,
            @RequestParam(required=false) String status,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("shopUserId");
        List<ShopOrderVO> list = shopOrderService.getMyOrders(userId, page, size, status);
        long total = shopOrderService.countMyOrders(userId, status);
        PageData<ShopOrderVO> pd = new PageData<>();
        pd.setTotal(total);
        pd.setRecords(list);
        return Result.success(pd);
    }

    @GetMapping("/{id}")
    public Result<ShopOrderVO> detail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("shopUserId");
        return Result.success(shopOrderService.getOrderDetail(userId, id));
    }

    @PatchMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("shopUserId");
        shopOrderService.cancelOrder(userId, id, ownerUserId);
        return Result.success("订单已取消", null);
    }

    @PatchMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("shopUserId");
        shopOrderService.confirmReceive(userId, id);
        return Result.success("确认收货成功", null);
    }
}
