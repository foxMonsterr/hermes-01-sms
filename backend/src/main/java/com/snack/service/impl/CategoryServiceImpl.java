package com.snack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.snack.common.BusinessException;
import com.snack.dto.CategoryCreateDTO;
import com.snack.dto.CategoryUpdateDTO;
import com.snack.entity.Category;
import com.snack.entity.Snack;
import com.snack.mapper.CategoryMapper;
import com.snack.mapper.SnackMapper;
import com.snack.service.CategoryService;
import com.snack.vo.CategoryVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现（用户隔离）
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final SnackMapper snackMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper, SnackMapper snackMapper) {
        this.categoryMapper = categoryMapper;
        this.snackMapper = snackMapper;
    }

    @Override
    public List<CategoryVO> listCategories(Long currentUserId) {
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getUserId, currentUserId)
                        .orderByAsc(Category::getSortOrder)
        );

        // 统计每个分类的零食数量
        List<Long> categoryIds = categories.stream().map(Category::getId).toList();
        Map<Long, Long> countMap = Map.of();
        if (!categoryIds.isEmpty()) {
            List<Snack> snacks = snackMapper.selectList(
                    new LambdaQueryWrapper<Snack>()
                            .eq(Snack::getUserId, currentUserId)
                            .in(Snack::getCategoryId, categoryIds)
            );
            countMap = snacks.stream()
                    .collect(Collectors.groupingBy(Snack::getCategoryId, Collectors.counting()));
        }

        Map<Long, Long> finalCountMap = countMap;
        return categories.stream().map(c -> {
            CategoryVO vo = new CategoryVO();
            vo.setId(c.getId());
            vo.setName(c.getName());
            vo.setIcon(c.getIcon());
            vo.setSortOrder(c.getSortOrder());
            vo.setSnackCount(finalCountMap.getOrDefault(c.getId(), 0L));
            vo.setCreateTime(c.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public CategoryVO getCategoryDetail(Long currentUserId, Long id) {
        Category category = findOwnCategory(currentUserId, id);
        return toVO(category, currentUserId);
    }

    @Override
    public CategoryVO createCategory(Long currentUserId, CategoryCreateDTO dto) {
        // 检查同名
        checkDuplicateName(currentUserId, dto.getName(), null);

        Category category = new Category();
        category.setUserId(currentUserId);
        category.setName(dto.getName());
        category.setIcon(dto.getIcon() != null ? dto.getIcon() : "");
        category.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        categoryMapper.insert(category);

        return toVO(category, currentUserId);
    }

    @Override
    public CategoryVO updateCategory(Long currentUserId, Long id, CategoryUpdateDTO dto) {
        Category category = findOwnCategory(currentUserId, id);

        // 检查同名（排除自己）
        checkDuplicateName(currentUserId, dto.getName(), id);

        category.setName(dto.getName());
        category.setIcon(dto.getIcon() != null ? dto.getIcon() : category.getIcon());
        category.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : category.getSortOrder());
        categoryMapper.updateById(category);

        return toVO(category, currentUserId);
    }

    @Override
    public void deleteCategory(Long currentUserId, Long id) {
        Category category = findOwnCategory(currentUserId, id);

        // 检查分类下是否有当前用户的零食
        long snackCount = snackMapper.selectCount(
                new LambdaQueryWrapper<Snack>()
                        .eq(Snack::getUserId, currentUserId)
                        .eq(Snack::getCategoryId, id)
        );
        if (snackCount > 0) {
            throw BusinessException.badRequest(
                    "该分类下有 " + snackCount + " 个零食，请先删除或转移零食");
        }

        categoryMapper.deleteById(id);
    }

    // ---------- private helpers ----------

    /**
     * 查找当前用户自己的分类，不存在 → 404
     */
    private Category findOwnCategory(Long currentUserId, Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null || !category.getUserId().equals(currentUserId)) {
            throw BusinessException.notFound("资源不存在");
        }
        return category;
    }

    /**
     * 检查同一用户下同名未删除分类
     */
    private void checkDuplicateName(Long currentUserId, String name, Long excludeId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<Category>()
                .eq(Category::getUserId, currentUserId)
                .eq(Category::getName, name);
        if (excludeId != null) {
            wrapper.ne(Category::getId, excludeId);
        }
        if (categoryMapper.exists(wrapper)) {
            throw BusinessException.conflict("分类名称已存在");
        }
    }

    private CategoryVO toVO(Category category, Long currentUserId) {
        // 零食数量统计（复用查询逻辑时用）
        long snackCount = snackMapper.selectCount(
                new LambdaQueryWrapper<Snack>()
                        .eq(Snack::getUserId, currentUserId)
                        .eq(Snack::getCategoryId, category.getId())
        );

        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setIcon(category.getIcon());
        vo.setSortOrder(category.getSortOrder());
        vo.setSnackCount(snackCount);
        vo.setCreateTime(category.getCreateTime());
        return vo;
    }
}
