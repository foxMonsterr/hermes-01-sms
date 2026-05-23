package com.snack;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 零食管理系统 — 启动入口
 */
@SpringBootApplication
@MapperScan("com.snack.mapper")
public class SnackApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnackApplication.class, args);
    }
}
