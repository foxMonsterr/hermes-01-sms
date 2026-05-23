package com.snack.service;

import com.snack.dto.CategoryCreateDTO;
import com.snack.dto.CategoryUpdateDTO;
import com.snack.vo.CategoryVO;

import java.util.List;

/**
 * 分类服务（用户隔离）
 */
public interface CategoryService {

    List<CategoryVO> listCategories(Long currentUserId);

    CategoryVO getCategoryDetail(Long currentUserId, Long id);

    CategoryVO createCategory(Long currentUserId, CategoryCreateDTO dto);

    CategoryVO updateCategory(Long currentUserId, Long id, CategoryUpdateDTO dto);

    void deleteCategory(Long currentUserId, Long id);
}
