package com.snack.service;

import com.snack.common.PageData;
import com.snack.dto.QuantityAdjustDTO;
import com.snack.dto.QuantityUpdateDTO;
import com.snack.dto.SnackCreateDTO;
import com.snack.dto.SnackQueryDTO;
import com.snack.dto.SnackUpdateDTO;
import com.snack.vo.QuantityUpdateVO;
import com.snack.vo.SnackPageVO;
import com.snack.vo.SnackVO;

import java.util.List;

/**
 * 零食服务（用户隔离）
 */
public interface SnackService {

    PageData<SnackPageVO> page(Long currentUserId, SnackQueryDTO query);

    SnackVO getDetail(Long currentUserId, Long id);

    SnackVO create(Long currentUserId, SnackCreateDTO dto);

    SnackVO update(Long currentUserId, Long id, SnackUpdateDTO dto);

    QuantityUpdateVO updateQuantity(Long currentUserId, Long id, QuantityUpdateDTO dto);

    QuantityUpdateVO adjustQuantity(Long currentUserId, Long id, QuantityAdjustDTO dto);

    void delete(Long currentUserId, Long id);

    void batchDelete(Long currentUserId, List<Long> ids);
}
