package com.snack.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j / OpenAPI 3 文档配置
 */
@Configuration
@EnableKnife4j
public class Knife4jConfig {

    @Bean
    public io.swagger.v3.oas.models.OpenAPI openAPI() {
        return new io.swagger.v3.oas.models.OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("零食管理系统 API")
                        .version("1.0.0")
                        .description("零食管理系统 — 多用户库存管理"));
    }
}
