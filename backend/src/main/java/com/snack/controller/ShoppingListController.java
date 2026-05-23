package com.snack.controller;

import com.snack.common.PageData;
import com.snack.common.Result;
import com.snack.dto.*;
import com.snack.service.ShoppingListService;
import com.snack.vo.ShoppingListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "采购清单", description = "采购规划与一键入库")
@RestController
@RequestMapping("/api/shopping-list")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    public ShoppingListController(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    @Operation(summary = "清单列表", description = "支持按状态筛选和关键字搜索")
    @GetMapping
    public Result<PageData<ShoppingListVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        return Result.success(shoppingListService.page(currentUserId, page, size, status, keyword));
    }

    @Operation(summary = "新增清单项")
    @PostMapping
    public Result<ShoppingListVO> create(@Valid @RequestBody ShoppingListCreateDTO dto,
                                          HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        return Result.success(shoppingListService.create(currentUserId, dto));
    }

    @Operation(summary = "更新清单项", description = "仅 PENDING 状态可编辑")
    @PutMapping("/{id}")
    public Result<ShoppingListVO> update(@PathVariable Long id,
                                          @Valid @RequestBody ShoppingListUpdateDTO dto,
                                          HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        return Result.success(shoppingListService.update(currentUserId, id, dto));
    }

    @Operation(summary = "更新状态", description = "仅允许 PENDING → CANCELLED")
    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                      @Valid @RequestBody ShoppingListStatusDTO dto,
                                      HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        shoppingListService.updateStatus(currentUserId, id, dto);
        return Result.success();
    }

    @Operation(summary = "删除清单项", description = "逻辑删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        shoppingListService.delete(currentUserId, id);
        return Result.success();
    }

    @Operation(summary = "低库存一键生成", description = "将 quantity≤2 的零食自动生成采购清单")
    @PostMapping("/from-low-stock")
    public Result<Map<String, Integer>> fromLowStock(HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        return Result.success(shoppingListService.fromLowStock(currentUserId));
    }

    @Operation(summary = "一键入库", description = "采购完成，自动入库并生成流水（事务）")
    @PostMapping("/{id}/stock-in")
    public Result<Map<String, Object>> stockIn(@PathVariable Long id,
                                                @Valid @RequestBody ShoppingListStockInDTO dto,
                                                HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        return Result.success(shoppingListService.stockIn(currentUserId, id, dto));
    }
}
