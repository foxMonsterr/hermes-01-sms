package com.snack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.snack.entity.Snack;
import com.snack.entity.StockRecord;
import com.snack.mapper.SnackMapper;
import com.snack.mapper.StockRecordMapper;
import com.snack.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportServiceImpl implements ExportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SnackMapper snackMapper;
    private final StockRecordMapper stockRecordMapper;

    public ExportServiceImpl(SnackMapper snackMapper, StockRecordMapper stockRecordMapper) {
        this.snackMapper = snackMapper;
        this.stockRecordMapper = stockRecordMapper;
    }

    @Override
    public void exportSnacks(Long currentUserId, String keyword, Long categoryId,
                              String expiryStatus, HttpServletResponse response) {
        LambdaQueryWrapper<Snack> wrapper = new LambdaQueryWrapper<Snack>()
                .eq(Snack::getUserId, currentUserId);

        if (keyword != null && !keyword.isBlank()) wrapper.like(Snack::getName, keyword);
        if (categoryId != null) wrapper.eq(Snack::getCategoryId, categoryId);
        if (expiryStatus != null && !expiryStatus.isBlank()) {
            LocalDate today = LocalDate.now();
            switch (expiryStatus) {
                case "expired":
                    wrapper.isNotNull(Snack::getExpiryDate).lt(Snack::getExpiryDate, today);
                    break;
                case "soon":
                    wrapper.isNotNull(Snack::getExpiryDate)
                           .ge(Snack::getExpiryDate, today)
                           .le(Snack::getExpiryDate, today.plusDays(30));
                    break;
                case "fresh":
                    wrapper.and(w -> w.isNull(Snack::getExpiryDate)
                            .or().gt(Snack::getExpiryDate, today.plusDays(30)));
                    break;
            }
        }

        List<Snack> snacks = snackMapper.selectList(wrapper);

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("零食清单");
            Row header = sheet.createRow(0);
            String[] headers = {"ID", "名称", "分类ID", "数量", "单位", "单价", "采购日期", "过期日期", "备注", "创建时间"};
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (Snack s : snacks) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(s.getId());
                row.createCell(1).setCellValue(s.getName());
                row.createCell(2).setCellValue(s.getCategoryId() != null ? s.getCategoryId().toString() : "");
                row.createCell(3).setCellValue(s.getQuantity());
                row.createCell(4).setCellValue(s.getUnit());
                row.createCell(5).setCellValue(s.getPrice() != null ? s.getPrice().toString() : "");
                row.createCell(6).setCellValue(s.getPurchaseDate() != null ? s.getPurchaseDate().format(DATE_FMT) : "");
                row.createCell(7).setCellValue(s.getExpiryDate() != null ? s.getExpiryDate().format(DATE_FMT) : "");
                row.createCell(8).setCellValue(s.getNotes());
                row.createCell(9).setCellValue(s.getCreateTime() != null ? s.getCreateTime().format(DATETIME_FMT) : "");
            }

            setResponse(response, "snacks_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        }
    }

    @Override
    public void exportStockRecords(Long currentUserId, Long snackId, String changeType,
                                    String startDate, String endDate, HttpServletResponse response) {
        LambdaQueryWrapper<StockRecord> wrapper = new LambdaQueryWrapper<StockRecord>()
                .eq(StockRecord::getUserId, currentUserId);

        if (snackId != null) wrapper.eq(StockRecord::getSnackId, snackId);
        if (changeType != null && !changeType.isBlank()) wrapper.eq(StockRecord::getChangeType, changeType);
        if (startDate != null && !startDate.isBlank()) {
            wrapper.ge(StockRecord::getCreateTime, LocalDate.parse(startDate, DATE_FMT).atStartOfDay());
        }
        if (endDate != null && !endDate.isBlank()) {
            wrapper.le(StockRecord::getCreateTime, LocalDate.parse(endDate, DATE_FMT).atTime(LocalTime.MAX));
        }
        wrapper.orderByDesc(StockRecord::getCreateTime);

        List<StockRecord> records = stockRecordMapper.selectList(wrapper);

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("库存流水");
            Row header = sheet.createRow(0);
            String[] headers = {"ID", "零食名称", "变动类型", "变动数量", "变动前", "变动后", "备注", "时间"};
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (StockRecord r : records) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.getId());
                row.createCell(1).setCellValue(r.getSnackName());
                row.createCell(2).setCellValue(r.getChangeType());
                row.createCell(3).setCellValue(r.getChangeQty());
                row.createCell(4).setCellValue(r.getBeforeQty());
                row.createCell(5).setCellValue(r.getAfterQty());
                row.createCell(6).setCellValue(r.getRemark());
                row.createCell(7).setCellValue(r.getCreateTime() != null ? r.getCreateTime().format(DATETIME_FMT) : "");
            }

            setResponse(response, "stock_records_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        }
    }

    private void setResponse(HttpServletResponse response, String filename) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
    }
}
