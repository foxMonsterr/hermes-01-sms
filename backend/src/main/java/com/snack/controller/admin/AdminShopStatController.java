package com.snack.controller.admin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.snack.common.Result;
import com.snack.entity.ShopOrder;
import com.snack.entity.ShopOrderItem;
import com.snack.mapper.ShopOrderMapper;
import com.snack.mapper.ShopOrderItemMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/shop-statistics")
public class AdminShopStatController {
    private final ShopOrderMapper orderMapper;
    private final ShopOrderItemMapper orderItemMapper;
    private final Long ownerUserId;

    public AdminShopStatController(ShopOrderMapper orderMapper, ShopOrderItemMapper orderItemMapper,
            @Value("${shop.owner-user-id}") Long ownerUserId) {
        this.orderMapper=orderMapper; this.orderItemMapper=orderItemMapper; this.ownerUserId=ownerUserId;
    }

    @GetMapping("/sales")
    public Result<Map<String,Object>> sales(@RequestParam(defaultValue="7d") String period) {
        int days = switch(period){case"7d"->7;case"30d"->30;case"90d"->90;default->7;};
        LocalDate end = LocalDate.now(); LocalDate start = end.minusDays(days-1);
        LocalDateTime startDt = start.atStartOfDay(); LocalDateTime endDt = end.plusDays(1).atStartOfDay();

        // 有效订单（非取消）
        List<ShopOrder> orders = orderMapper.selectList(new LambdaQueryWrapper<ShopOrder>()
            .eq(ShopOrder::getOwnerUserId, ownerUserId).eq(ShopOrder::getIsDeleted, 0)
            .ne(ShopOrder::getStatus, "CANCELLED")
            .ge(ShopOrder::getCreateTime, startDt).le(ShopOrder::getCreateTime, endDt));

        BigDecimal totalAmount = orders.stream().map(ShopOrder::getTotalAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        long orderCount = orders.size();
        BigDecimal avgAmount = orderCount>0?totalAmount.divide(BigDecimal.valueOf(orderCount),2, java.math.RoundingMode.HALF_UP):BigDecimal.ZERO;

        // 今日
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        List<ShopOrder> todayOrders = orders.stream().filter(o->o.getCreateTime()!=null&&!o.getCreateTime().isBefore(todayStart)).toList();
        BigDecimal todayAmount = todayOrders.stream().map(ShopOrder::getTotalAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        // 热销商品 Top 5
        Map<Long,long[]> topMap = new LinkedHashMap<>();
        for (ShopOrder o : orders) {
            List<ShopOrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<ShopOrderItem>().eq(ShopOrderItem::getOrderId, o.getId()));
            for (ShopOrderItem item : items) {
                topMap.compute(item.getSnackId(), (k,v)->{
                    if (v==null) return new long[]{item.getQuantity(), item.getSubtotal().longValue()};
                    v[0]+=item.getQuantity(); v[1]+=item.getSubtotal().longValue(); return v;
                });
            }
        }
        List<Map<String,Object>> topProducts = topMap.entrySet().stream()
            .sorted((a,b)->Long.compare(b.getValue()[0], a.getValue()[0])).limit(5)
            .map(e->{
                Map<String,Object> m=new HashMap<>(); m.put("snackId",e.getKey());
                ShopOrderItem sample = orderItemMapper.selectList(new LambdaQueryWrapper<ShopOrderItem>().eq(ShopOrderItem::getSnackId,e.getKey()).last("LIMIT 1")).stream().findFirst().orElse(null);
                m.put("snackName",sample!=null?sample.getSnackName():"未知");
                m.put("quantity",e.getValue()[0]); m.put("amount",e.getValue()[1]); return m;
            }).collect(Collectors.toList());

        // 每日销售趋势
        List<Map<String,Object>> dailySales = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            LocalDate fd = d;
            List<ShopOrder> dayOrders = orders.stream().filter(o->o.getCreateTime()!=null&&o.getCreateTime().toLocalDate().equals(fd)).toList();
            Map<String,Object> ds = new HashMap<>();
            ds.put("date",d.format(fmt));
            ds.put("amount", dayOrders.stream().map(ShopOrder::getTotalAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add));
            ds.put("orderCount",dayOrders.size());
            dailySales.add(ds);
        }

        Map<String,Object> result = new HashMap<>();
        result.put("totalSalesAmount", totalAmount);
        result.put("todaySalesAmount", todayAmount);
        result.put("totalOrderCount", orderCount);
        result.put("todayOrderCount", todayOrders.size());
        result.put("averageOrderAmount", avgAmount);
        result.put("topProducts", topProducts);
        result.put("dailySales", dailySales);
        return Result.success(result);
    }
}
