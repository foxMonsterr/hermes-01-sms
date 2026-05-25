package com.snack.controller;

import com.snack.common.BusinessException;
import com.snack.common.PageData;
import com.snack.common.Result;
import com.snack.dto.*;
import com.snack.entity.Snack;
import com.snack.mapper.SnackMapper;
import com.snack.service.SnackService;
import com.snack.vo.QuantityUpdateVO;
import com.snack.vo.SnackPageVO;
import com.snack.vo.SnackVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 零食管理接口（用户隔离）
 */
@Tag(name = "零食管理", description = "零食的增删改查与库存操作")
@RestController
@RequestMapping("/api/snacks")
public class SnackController {

    private final SnackService snackService;
    private final SnackMapper snackMapper;

    public SnackController(SnackService snackService, SnackMapper snackMapper) {
        this.snackService = snackService;
        this.snackMapper = snackMapper;
    }

    @Operation(summary = "分页列表", description = "支持关键字搜索、分类筛选、过期状态筛选")
    @GetMapping
    public Result<PageData<SnackPageVO>> list(SnackQueryDTO query, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        return Result.success(snackService.page(currentUserId, query));
    }

    @Operation(summary = "零食详情", description = "获取当前用户指定零食的详情")
    @GetMapping("/{id}")
    public Result<SnackVO> detail(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        return Result.success(snackService.getDetail(currentUserId, id));
    }

    @Operation(summary = "新增零食", description = "分类必须属于当前用户")
    @PostMapping
    public Result<SnackVO> create(@Valid @RequestBody SnackCreateDTO dto,
                                   HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        return Result.success(snackService.create(currentUserId, dto));
    }

    @Operation(summary = "更新零食", description = "只能更新自己的零食")
    @PutMapping("/{id}")
    public Result<SnackVO> update(@PathVariable Long id,
                                   @Valid @RequestBody SnackUpdateDTO dto,
                                   HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        return Result.success(snackService.update(currentUserId, id, dto));
    }

    @Operation(summary = "库存增减", description = "delta > 0 增加, delta < 0 减少, 不能为 0；v4.0 自动生成库存流水")
    @PatchMapping("/{id}/quantity")
    public Result<QuantityUpdateVO> updateQuantity(@PathVariable Long id,
                                                     @Valid @RequestBody QuantityUpdateDTO dto,
                                                     HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        return Result.success(snackService.updateQuantity(currentUserId, id, dto));
    }

    @Operation(summary = "盘点调整库存", description = "将库存调整为指定值，自动计算变动量并生成 ADJUST 流水")
    @PatchMapping("/{id}/quantity/adjust")
    public Result<QuantityUpdateVO> adjustQuantity(@PathVariable Long id,
                                                     @Valid @RequestBody QuantityAdjustDTO dto,
                                                     HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        return Result.success(snackService.adjustQuantity(currentUserId, id, dto));
    }

    @Operation(summary = "删除零食", description = "逻辑删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        snackService.delete(currentUserId, id);
        return Result.success();
    }

    @Operation(summary = "批量删除", description = "只删除当前用户自己的零食")
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody BatchDeleteDTO dto,
                                     HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        snackService.batchDelete(currentUserId, dto.getIds());
        return Result.success();
    }

    @Operation(summary = "上架/下架", description = "v7.0: 切换零食在小卖铺的上架状态")
    @PatchMapping("/{id}/shelf")
    public Result<Void> toggleShelf(@PathVariable Long id, @RequestBody java.util.Map<String,Boolean> body,
                                     HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        Boolean onShelf = body.get("isOnShelf");
        if (onShelf==null) throw BusinessException.badRequest("缺少 isOnShelf 参数");
        Snack snack = snackMapper.selectById(id);
        if (snack==null||!snack.getUserId().equals(currentUserId))
            throw BusinessException.notFound("零食不存在");
        if (snack.getPrice()==null && Boolean.TRUE.equals(onShelf))
            throw BusinessException.badRequest("商品价格不能为空，设置价格后才能上架");
        snack.setIsOnShelf(onShelf?1:0);
        if (Boolean.TRUE.equals(onShelf)) snack.setShelfTime(LocalDateTime.now());
        snackMapper.updateById(snack);
        return Result.success(onShelf?"已上架":"已下架", null);
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("currentUserId");
    }
}
