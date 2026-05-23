package com.snack.controller;

import com.snack.common.PageData;
import com.snack.common.Result;
import com.snack.service.NotificationService;
import com.snack.vo.NotificationVO;
import com.snack.vo.UnreadCountVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "消息提醒", description = "过期/低库存提醒")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "提醒列表", description = "支持按类型和已读状态筛选")
    @GetMapping
    public Result<PageData<NotificationVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer isRead,
            HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        return Result.success(notificationService.page(currentUserId, page, size, type, isRead));
    }

    @Operation(summary = "未读数量")
    @GetMapping("/unread-count")
    public Result<UnreadCountVO> unreadCount(HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        return Result.success(notificationService.unreadCount(currentUserId));
    }

    @Operation(summary = "生成提醒", description = "扫描当前用户零食，生成过期和低库存提醒")
    @PostMapping("/generate")
    public Result<Map<String, Integer>> generate(HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        return Result.success(notificationService.generate(currentUserId));
    }

    @Operation(summary = "标记已读")
    @PatchMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        notificationService.markRead(currentUserId, id);
        return Result.success();
    }

    @Operation(summary = "全部已读")
    @PatchMapping("/read-all")
    public Result<Void> markAllRead(HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        notificationService.markAllRead(currentUserId);
        return Result.success();
    }

    @Operation(summary = "删除提醒")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        notificationService.delete(currentUserId, id);
        return Result.success();
    }
}
