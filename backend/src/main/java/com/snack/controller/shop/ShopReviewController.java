package com.snack.controller.shop;
import com.snack.common.BusinessException;
import com.snack.common.Result;
import com.snack.entity.ShopUser;
import com.snack.mapper.ShopUserMapper;
import com.snack.security.ShopJwtUtil;
import com.snack.service.ShopReviewService;
import com.snack.vo.ShopReviewVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shop")
public class ShopReviewController {
    private final ShopReviewService reviewService;
    private final ShopUserMapper shopUserMapper;
    private final ShopJwtUtil shopJwtUtil;
    public ShopReviewController(ShopReviewService reviewService, ShopUserMapper shopUserMapper, ShopJwtUtil shopJwtUtil) {
        this.reviewService = reviewService; this.shopUserMapper = shopUserMapper; this.shopJwtUtil = shopJwtUtil;
    }

    @GetMapping("/products/{id}/reviews")
    public Result<List<ShopReviewVO>> list(@PathVariable Long id) {
        return Result.success(reviewService.listBySnackId(id));
    }

    @PostMapping("/products/{id}/reviews")
    public Result<ShopReviewVO> create(@PathVariable Long id, @RequestBody Map<String,String> body,
                                        HttpServletRequest request) {
        Long userId = resolveUserId(request);
        ShopUser u = shopUserMapper.selectById(userId);
        String username = u!=null ? u.getNickname()!=null&&!u.getNickname().isBlank()?u.getNickname():u.getUsername() : "用户"+userId;
        return Result.success(reviewService.create(userId, username, id,
            Long.valueOf(String.valueOf(body.get("orderId"))), body.get("content")));
    }

    @DeleteMapping("/reviews/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        reviewService.delete(resolveUserId(request), id);
        return Result.success("删除成功", null);
    }

    private Long resolveUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth==null||!auth.startsWith("Bearer ")) throw BusinessException.unauthorized("请先登录");
        String token = auth.substring(7);
        if (!shopJwtUtil.validateToken(token)) throw BusinessException.unauthorized("登录已过期");
        return shopJwtUtil.getUserId(token);
    }
}
