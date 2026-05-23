package com.snack.service;
import com.snack.common.PageData;
import com.snack.entity.Supplier;
import java.util.List;
import java.util.Map;

public interface SupplierService {
    PageData<Supplier> page(Long userId, int page, int size, String keyword);
    List<Map<String, Object>> options(Long userId);
    Supplier getById(Long userId, Long id);
    Supplier create(Long userId, Supplier supplier);
    Supplier update(Long userId, Long id, Supplier supplier);
    void delete(Long userId, Long id);
    Map<String, Object> stats(Long userId, Long id, String startDate, String endDate);
}
