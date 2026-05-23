package com.snack.controller;

import com.snack.common.Result;
import com.snack.service.SystemConfigService;
import com.snack.vo.SystemConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@Tag(name = "系统设置", description = "用户级系统配置")
@RestController
@RequestMapping("/api/settings")
public class SettingsController {
    private final SystemConfigService configService;
    public SettingsController(SystemConfigService configService) { this.configService = configService; }

    @Operation(summary = "获取配置")
    @GetMapping("/config")
    public Result<SystemConfigVO> getConfig(HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("currentUserId");
        return Result.success(configService.getConfig(uid));
    }

    @Operation(summary = "更新配置")
    @PutMapping("/config")
    public Result<SystemConfigVO> updateConfig(@RequestBody SystemConfigVO vo, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("currentUserId");
        return Result.success(configService.updateConfig(uid, vo));
    }

    @Operation(summary = "恢复默认")
    @PostMapping("/config/reset")
    public Result<SystemConfigVO> resetConfig(HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("currentUserId");
        return Result.success(configService.resetConfig(uid));
    }
}
