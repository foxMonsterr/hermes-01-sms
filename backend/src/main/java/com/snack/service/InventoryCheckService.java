package com.snack.service;
import com.snack.common.PageData;
import com.snack.dto.InventoryCheckDTO;
import com.snack.entity.InventoryCheckRecord;
import java.util.Map;

public interface InventoryCheckService {
    PageData<InventoryCheckRecord> page(Long userId, int page, int size, String startDate, String endDate);
    Map<String, Object> check(Long userId, Long snackId, InventoryCheckDTO dto);
    Map<String, Object> stats(Long userId, String startDate, String endDate);
}
