package com.snack.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snack.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 拦截器 — 解析 token，设置 currentUserId / currentUsername
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");

        // 无 token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            write401(response, "未登录或 token 已过期");
            return false;
        }

        String token = authHeader.substring(7);

        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            request.setAttribute("currentUserId", userId);
            request.setAttribute("currentUsername", username);
            return true;
        } catch (Exception e) {
            write401(response, "未登录或 token 已过期");
            return false;
        }
    }

    private void write401(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(Result.error(401, msg))
        );
    }
}
