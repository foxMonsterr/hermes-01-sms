package com.snack.controller;

import com.snack.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Tag(name = "系统管理", description = "备份、健康检查、版本信息")
@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Operation(summary = "数据备份下载", description = "仅 dev/demo 环境")
    @GetMapping("/backup")
    public void backup(HttpServletResponse response) throws Exception {
        Path dbPath = Paths.get("./data/snack.db");
        if (!dbPath.toFile().exists()) throw new RuntimeException(new Exception("数据库文件不存在"));
        String filename = "snack_backup_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".db";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
        Files.copy(dbPath, response.getOutputStream());
    }

    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("database", new File("./data/snack.db").exists() ? "UP" : "DOWN");
        data.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return Result.success(data);
    }

    @Operation(summary = "版本信息")
    @GetMapping("/version")
    public Result<Map<String, String>> version() {
        Map<String, String> data = new HashMap<>();
        data.put("name", "零食管理系统");
        data.put("version", "v5.0");
        data.put("backend", "Spring Boot 3");
        data.put("frontend", "Vue 3");
        return Result.success(data);
    }
}
