package com.snack.controller;

import com.snack.common.PageData;
import com.snack.common.Result;
import com.snack.entity.Supplier;
import com.snack.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Tag(name = "供应商管理", description = "供应商CRUD与采购统计")
@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    private final SupplierService service;
    public SupplierController(SupplierService s) { this.service = s; }

    @GetMapping
    public Result<PageData<Supplier>> page(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String keyword,
            HttpServletRequest req) {
        Long uid = (Long) req.getAttribute("currentUserId");
        return Result.success(service.page(uid, page, size, keyword));
    }

    @GetMapping("/options")
    public Result<List<Map<String, Object>>> options(HttpServletRequest req) {
        return Result.success(service.options((Long) req.getAttribute("currentUserId")));
    }

    @GetMapping("/{id}")
    public Result<Supplier> detail(@PathVariable Long id, HttpServletRequest req) {
        return Result.success(service.getById((Long) req.getAttribute("currentUserId"), id));
    }

    @GetMapping("/{id}/stats")
    public Result<Map<String, Object>> stats(@PathVariable Long id,
            @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
            HttpServletRequest req) {
        return Result.success(service.stats((Long) req.getAttribute("currentUserId"), id, startDate, endDate));
    }

    @PostMapping
    public Result<Supplier> create(@RequestBody Supplier s, HttpServletRequest req) {
        return Result.success(service.create((Long) req.getAttribute("currentUserId"), s));
    }

    @PutMapping("/{id}")
    public Result<Supplier> update(@PathVariable Long id, @RequestBody Supplier s, HttpServletRequest req) {
        return Result.success(service.update((Long) req.getAttribute("currentUserId"), id, s));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest req) {
        service.delete((Long) req.getAttribute("currentUserId"), id);
        return Result.success();
    }
}
