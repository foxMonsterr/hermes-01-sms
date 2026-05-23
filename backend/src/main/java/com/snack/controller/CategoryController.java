package com.snack.controller;

import com.snack.common.Result;
import com.snack.dto.CategoryCreateDTO;
import com.snack.dto.CategoryUpdateDTO;
import com.snack.service.CategoryService;
import com.snack.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理接口（用户隔离）
 */
@Tag(name = "分类管理", description = "零食分类的增删改查")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "分类列表", description = "获取当前用户的所有分类")
    @GetMapping
    public Result<List<CategoryVO>> list(HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        return Result.success(categoryService.listCategories(currentUserId));
    }

    @Operation(summary = "分类详情", description = "获取当前用户指定分类的详情")
    @GetMapping("/{id}")
    public Result<CategoryVO> detail(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        return Result.success(categoryService.getCategoryDetail(currentUserId, id));
    }

    @Operation(summary = "新增分类", description = "为当前用户创建分类，同名检查")
    @PostMapping
    public Result<CategoryVO> create(@Valid @RequestBody CategoryCreateDTO dto,
                                      HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        return Result.success(categoryService.createCategory(currentUserId, dto));
    }

    @Operation(summary = "更新分类", description = "更新当前用户的分类信息")
    @PutMapping("/{id}")
    public Result<CategoryVO> update(@PathVariable Long id,
                                      @Valid @RequestBody CategoryUpdateDTO dto,
                                      HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        return Result.success(categoryService.updateCategory(currentUserId, id, dto));
    }

    @Operation(summary = "删除分类", description = "删除当前用户的分类，有零食时不允许删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        categoryService.deleteCategory(currentUserId, id);
        return Result.success();
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("currentUserId");
    }
}
