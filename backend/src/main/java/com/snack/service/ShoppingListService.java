package com.snack.service;

import com.snack.common.PageData;
import com.snack.dto.*;
import com.snack.vo.ShoppingListVO;

import java.util.Map;

public interface ShoppingListService {

    PageData<ShoppingListVO> page(Long currentUserId, Integer page, Integer size, String status, String keyword);

    ShoppingListVO create(Long currentUserId, ShoppingListCreateDTO dto);

    ShoppingListVO update(Long currentUserId, Long id, ShoppingListUpdateDTO dto);

    void updateStatus(Long currentUserId, Long id, ShoppingListStatusDTO dto);

    void delete(Long currentUserId, Long id);

    Map<String, Integer> fromLowStock(Long currentUserId);

    Map<String, Object> stockIn(Long currentUserId, Long id, ShoppingListStockInDTO dto);
}
