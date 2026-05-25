package com.snack.security;
import com.snack.common.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ShopJwtInterceptor implements HandlerInterceptor {
    private final ShopJwtUtil shopJwtUtil;

    public ShopJwtInterceptor(ShopJwtUtil shopJwtUtil) {
        this.shopJwtUtil = shopJwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw BusinessException.unauthorized("请先登录");
        }
        String token = auth.substring(7);
        if (!shopJwtUtil.validateToken(token)) {
            throw BusinessException.unauthorized("登录已过期，请重新登录");
        }
        Long userId = shopJwtUtil.getUserId(token);
        request.setAttribute("shopUserId", userId);
        return true;
    }
}
