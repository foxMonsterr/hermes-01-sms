package com.snack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.snack.common.BusinessException;
import com.snack.entity.Category;
import com.snack.entity.Snack;
import com.snack.entity.StockRecord;
import com.snack.mapper.CategoryMapper;
import com.snack.mapper.SnackMapper;
import com.snack.mapper.StockRecordMapper;
import com.snack.service.StatisticsService;
import com.snack.vo.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计服务实现 v4.0（用户隔离）
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static final int LOW_STOCK_THRESHOLD = 2;  // v4.0: quantity ≤ 2

    private final SnackMapper snackMapper;
    private final CategoryMapper categoryMapper;
    private final StockRecordMapper stockRecordMapper;

    public StatisticsServiceImpl(SnackMapper snackMapper, CategoryMapper categoryMapper,
                                  StockRecordMapper stockRecordMapper) {
        this.snackMapper = snackMapper;
        this.categoryMapper = categoryMapper;
        this.stockRecordMapper = stockRecordMapper;
    }

    @Override
    public StatisticsOverviewVO overview(Long currentUserId) {
        List<Snack> snacks = snackMapper.selectList(
                new LambdaQueryWrapper<Snack>()
                        .eq(Snack::getUserId, currentUserId)
        );

        LocalDate today = LocalDate.now();
        LocalDate soon = today.plusDays(30);

        long totalSnackCount = snacks.size();
        long totalQuantity = snacks.stream().mapToLong(Snack::getQuantity).sum();
        long expiredCount = snacks.stream()
                .filter(s -> s.getExpiryDate() != null && s.getExpiryDate().isBefore(today))
                .count();
        long soonExpiredCount = snacks.stream()
                .filter(s -> s.getExpiryDate() != null
                        && !s.getExpiryDate().isBefore(today)
                        && !s.getExpiryDate().isAfter(soon))
                .count();
        long lowStockCount = snacks.stream()
                .filter(s -> s.getQuantity() <= LOW_STOCK_THRESHOLD)
                .count();

        long categoryCount = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getUserId, currentUserId)
        );

        // 库存总价值
        BigDecimal totalValue = snacks.stream()
                .filter(s -> s.getPrice() != null)
                .map(s -> s.getPrice().multiply(BigDecimal.valueOf(s.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 今日出入库（从 stock_record 统计）
        long todayInQty = sumChangeQtyForDate(currentUserId, "IN", today);
        long todayOutQty = Math.abs(sumChangeQtyForDate(currentUserId, "OUT", today));

        StatisticsOverviewVO vo = new StatisticsOverviewVO();
        vo.setTotalSnackCount(totalSnackCount);
        vo.setTotalQuantity(totalQuantity);
        vo.setExpiredCount(expiredCount);
        vo.setSoonExpiredCount(soonExpiredCount);
        vo.setLowStockCount(lowStockCount);
        vo.setCategoryCount(categoryCount);
        vo.setTotalValue(totalValue);
        vo.setTodayInQty(todayInQty);
        vo.setTodayOutQty(todayOutQty);
        return vo;
    }

    @Override
    public List<CategoryDistributionVO> categoryDistribution(Long currentUserId) {
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getUserId, currentUserId)
                        .orderByAsc(Category::getSortOrder)
        );

        List<Long> categoryIds = categories.stream().map(Category::getId).toList();

        List<Snack> snacks;
        if (categoryIds.isEmpty()) {
            snacks = List.of();
        } else {
            snacks = snackMapper.selectList(
                    new LambdaQueryWrapper<Snack>()
                            .eq(Snack::getUserId, currentUserId)
                            .in(Snack::getCategoryId, categoryIds)
            );
        }

        Map<Long, List<Snack>> grouped = snacks.stream()
                .collect(Collectors.groupingBy(Snack::getCategoryId));

        return categories.stream().map(c -> {
            List<Snack> catSnacks = grouped.getOrDefault(c.getId(), List.of());
            int snackCount = catSnacks.size();
            int totalQuantity = catSnacks.stream().mapToInt(Snack::getQuantity).sum();

            CategoryDistributionVO vo = new CategoryDistributionVO();
            vo.setCategoryId(c.getId());
            vo.setCategoryName(c.getName());
            vo.setSnackCount(snackCount);
            vo.setTotalQuantity(totalQuantity);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public StockTrendVO stockTrend(Long currentUserId, String period) {
        int days = switch (period) {
            case "7d" -> 7;
            case "30d" -> 30;
            case "90d" -> 90;
            default -> throw BusinessException.badRequest("period 参数不合法，合法值: 7d/30d/90d");
        };

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        // 查询周期内的 IN/OUT 流水
        List<StockRecord> records = stockRecordMapper.selectList(
                new LambdaQueryWrapper<StockRecord>()
                        .eq(StockRecord::getUserId, currentUserId)
                        .in(StockRecord::getChangeType, "IN", "OUT")
                        .ge(StockRecord::getCreateTime, startDate.atStartOfDay())
                        .le(StockRecord::getCreateTime, endDate.plusDays(1).atStartOfDay())
        );

        // 按日期分组统计
        Map<String, long[]> dailyMap = new LinkedHashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < days; i++) {
            dailyMap.put(startDate.plusDays(i).format(fmt), new long[]{0, 0});
        }

        long totalIn = 0, totalOut = 0;
        Map<Long, Long> outBySnack = new HashMap<>();  // snackId -> totalOut

        for (StockRecord r : records) {
            String dateKey = r.getCreateTime().toLocalDate().format(fmt);
            long[] arr = dailyMap.get(dateKey);
            if (arr != null) {
                if ("IN".equals(r.getChangeType())) {
                    arr[0] += r.getChangeQty();
                    totalIn += r.getChangeQty();
                } else {
                    long absQty = Math.abs(r.getChangeQty());
                    arr[1] += absQty;
                    totalOut += absQty;
                    outBySnack.merge(r.getSnackId(), absQty, Long::sum);
                }
            }
        }

        List<DailyDataVO> dailyData = dailyMap.entrySet().stream()
                .map(e -> new DailyDataVO(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .collect(Collectors.toList());

        // Top 5 消耗排行
        List<TopConsumedVO> topConsumed = outBySnack.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    TopConsumedVO vo = new TopConsumedVO();
                    // 从库存流水快照取名称
                    StockRecord latest = stockRecordMapper.selectOne(
                            new LambdaQueryWrapper<StockRecord>()
                                    .eq(StockRecord::getSnackId, e.getKey())
                                    .orderByDesc(StockRecord::getCreateTime)
                                    .last("LIMIT 1")
                    );
                    vo.setSnackName(latest != null ? latest.getSnackName() : "未知零食");
                    vo.setTotalOut(e.getValue());
                    return vo;
                })
                .collect(Collectors.toList());

        StockTrendVO vo = new StockTrendVO();
        vo.setPeriod(period);
        vo.setTotalIn(totalIn);
        vo.setTotalOut(totalOut);
        vo.setDailyData(dailyData);
        vo.setTopConsumed(topConsumed);
        return vo;
    }

    @Override
    public ValueStatsVO valueStats(Long currentUserId) {
        List<Snack> snacks = snackMapper.selectList(
                new LambdaQueryWrapper<Snack>()
                        .eq(Snack::getUserId, currentUserId)
                        .isNotNull(Snack::getPrice)
        );

        BigDecimal totalValue = snacks.stream()
                .map(s -> s.getPrice().multiply(BigDecimal.valueOf(s.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 按分类统计
        Map<Long, BigDecimal> catValueMap = new HashMap<>();
        Map<Long, String> catNameMap = new HashMap<>();
        for (Snack s : snacks) {
            BigDecimal val = s.getPrice().multiply(BigDecimal.valueOf(s.getQuantity()));
            catValueMap.merge(s.getCategoryId(), val, BigDecimal::add);
            if (!catNameMap.containsKey(s.getCategoryId())) {
                Category cat = categoryMapper.selectById(s.getCategoryId());
                catNameMap.put(s.getCategoryId(), cat != null ? cat.getName() : "未知");
            }
        }

        List<CategoryValueVO> categoryValues = catValueMap.entrySet().stream()
                .map(e -> {
                    CategoryValueVO cv = new CategoryValueVO();
                    cv.setCategoryName(catNameMap.getOrDefault(e.getKey(), "未知"));
                    cv.setTotalValue(e.getValue());
                    if (totalValue.compareTo(BigDecimal.ZERO) > 0) {
                        cv.setPercentage(e.getValue()
                                .multiply(BigDecimal.valueOf(100))
                                .divide(totalValue, 1, RoundingMode.HALF_UP));
                    } else {
                        cv.setPercentage(BigDecimal.ZERO);
                    }
                    return cv;
                })
                .sorted((a, b) -> b.getTotalValue().compareTo(a.getTotalValue()))
                .collect(Collectors.toList());

        // 最贵 Top 5
        List<PriciestSnackVO> priciest = snacks.stream()
                .sorted((a, b) -> {
                    BigDecimal va = a.getPrice().multiply(BigDecimal.valueOf(a.getQuantity()));
                    BigDecimal vb = b.getPrice().multiply(BigDecimal.valueOf(b.getQuantity()));
                    return vb.compareTo(va);
                })
                .limit(5)
                .map(s -> {
                    PriciestSnackVO pv = new PriciestSnackVO();
                    pv.setSnackName(s.getName());
                    pv.setTotalPrice(s.getPrice().multiply(BigDecimal.valueOf(s.getQuantity())));
                    return pv;
                })
                .collect(Collectors.toList());

        ValueStatsVO vo = new ValueStatsVO();
        vo.setTotalValue(totalValue);
        vo.setCategoryValues(categoryValues);
        vo.setPriciestSnacks(priciest);
        return vo;
    }

    @Override
    public java.util.Map<String, Object> consumptionAnalysis(Long currentUserId, String period) {
        int days = switch (period) { case "7d"->7; case "30d"->30; case "90d"->90; default->30; };
        java.time.LocalDate end = java.time.LocalDate.now(), start = end.minusDays(days - 1);

        var records = stockRecordMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StockRecord>()
                .eq(StockRecord::getUserId, currentUserId).eq(StockRecord::getChangeType, "OUT")
                .ge(StockRecord::getCreateTime, start.atStartOfDay())
                .le(StockRecord::getCreateTime, end.plusDays(1).atStartOfDay())
        );

        long totalQty = records.stream().mapToLong(r -> Math.abs(r.getChangeQty())).sum();
        double avgDaily = days > 0 ? (double) totalQty / days : 0;

        // estimated value
        java.math.BigDecimal estValue = java.math.BigDecimal.ZERO;
        java.util.Map<Long, Long> snackOut = new java.util.HashMap<>();
        java.util.Map<Long, Long> catOut = new java.util.HashMap<>();
        for (StockRecord r : records) {
            snackOut.merge(r.getSnackId(), (long) Math.abs(r.getChangeQty()), Long::sum);
            Snack s = snackMapper.selectById(r.getSnackId());
            if (s != null) {
                catOut.merge(s.getCategoryId(), (long) Math.abs(r.getChangeQty()), Long::sum);
                if (s.getPrice() != null) estValue = estValue.add(s.getPrice().multiply(java.math.BigDecimal.valueOf(Math.abs(r.getChangeQty()))));
            }
        }

        // top snacks
        var topSnacks = snackOut.entrySet().stream().sorted(java.util.Map.Entry.<Long,Long>comparingByValue().reversed()).limit(5).map(e->{
            var m = new java.util.HashMap<String,Object>(); StockRecord sr = stockRecordMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StockRecord>().eq(StockRecord::getSnackId,e.getKey()).orderByDesc(StockRecord::getCreateTime).last("LIMIT 1")); m.put("snackName",sr!=null?sr.getSnackName():"未知"); m.put("quantity",e.getValue()); return m;
        }).toList();

        // top categories
        var topCats = catOut.entrySet().stream().sorted(java.util.Map.Entry.<Long,Long>comparingByValue().reversed()).limit(5).map(e->{
            var m = new java.util.HashMap<String,Object>(); Category c = categoryMapper.selectById(e.getKey()); m.put("categoryName",c!=null?c.getName():"未知"); m.put("quantity",e.getValue()); return m;
        }).toList();

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("totalConsumedQty", totalQty);
        result.put("averageDailyConsumed", Math.round(avgDaily * 10) / 10.0);
        result.put("estimatedConsumedValue", estValue);
        result.put("topSnacks", topSnacks);
        result.put("topCategories", topCats);
        return result;
    }

    // ---------- private helpers ----------

    private long sumChangeQtyForDate(Long userId, String changeType, LocalDate date) {
        List<StockRecord> records = stockRecordMapper.selectList(
                new LambdaQueryWrapper<StockRecord>()
                        .eq(StockRecord::getUserId, userId)
                        .eq(StockRecord::getChangeType, changeType)
                        .ge(StockRecord::getCreateTime, date.atStartOfDay())
                        .lt(StockRecord::getCreateTime, date.plusDays(1).atStartOfDay())
        );
        return records.stream().mapToLong(r -> Math.abs(r.getChangeQty())).sum();
    }
}
