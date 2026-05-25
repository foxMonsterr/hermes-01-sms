package com.snack.config;

import com.snack.security.JwtInterceptor;
import com.snack.security.ShopJwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：CORS + JWT 拦截器
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final ShopJwtInterceptor shopJwtInterceptor;

    public WebMvcConfig(JwtInterceptor jwtInterceptor, ShopJwtInterceptor shopJwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
        this.shopJwtInterceptor = shopJwtInterceptor;
    }

    /**
     * 允许前端开发服务器 http://localhost:5173 跨域访问
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    /**
     * 注册 JWT 拦截器，放行认证接口和 Knife4j 资源
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/doc.html",
                        "/doc.html/**",
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/favicon.ico",
                        "/api/files/snacks/**",
                        "/api/files/avatars/**",
                        "/api/system/health",
                        "/api/system/version",
                        "/api/shop/**"
                );

        // v7.0 客户端 JWT 拦截器
        registry.addInterceptor(shopJwtInterceptor)
                .addPathPatterns(
                        "/api/shop/auth/profile",
                        "/api/shop/auth/password",
                        "/api/shop/cart/**",
                        "/api/shop/orders/**",
                        "/api/shop/reviews/**"
                )
                .excludePathPatterns(
                        "/api/shop/auth/login",
                        "/api/shop/auth/register",
                        "/api/shop/products/**",
                        "/api/shop/categories",
                        "/api/shop/announcements"
                );
    }
}
