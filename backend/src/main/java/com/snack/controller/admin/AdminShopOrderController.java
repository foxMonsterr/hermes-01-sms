package com.snack.controller.admin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.snack.common.BusinessException;
import com.snack.common.PageData;
import com.snack.common.Result;
import com.snack.entity.*;
import com.snack.mapper.*;
import com.snack.vo.ShopOrderItemVO;
import com.snack.vo.ShopOrderVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/shop-orders")
public class AdminShopOrderController {
    private final ShopOrderMapper orderMapper;
    private final ShopOrderItemMapper orderItemMapper;
    private final SnackMapper snackMapper;
    private final StockRecordMapper stockRecordMapper;
    private final Long ownerUserId;

    public AdminShopOrderController(ShopOrderMapper orderMapper, ShopOrderItemMapper orderItemMapper,
            SnackMapper snackMapper, StockRecordMapper stockRecordMapper,
            @Value("${shop.owner-user-id}") Long ownerUserId) {
        this.orderMapper=orderMapper; this.orderItemMapper=orderItemMapper;
        this.snackMapper=snackMapper; this.stockRecordMapper=stockRecordMapper;
        this.ownerUserId=ownerUserId;
    }

    @GetMapping
    public Result<PageData<ShopOrderVO>> list(
            @RequestParam(defaultValue="1") int page, @RequestParam(defaultValue="10") int size,
            @RequestParam(required=false) String status, @RequestParam(required=false) String keyword) {
        var q = new LambdaQueryWrapper<ShopOrder>()
            .eq(ShopOrder::getOwnerUserId, ownerUserId).eq(ShopOrder::getIsDeleted, 0)
            .orderByDesc(ShopOrder::getCreateTime);
        if (status!=null&&!status.isBlank()) q.eq(ShopOrder::getStatus, status);
        if (keyword!=null&&!keyword.isBlank()) q.like(ShopOrder::getOrderNo, keyword);
        Page<ShopOrder> p = orderMapper.selectPage(new Page<>(page,size), q);
        List<ShopOrderVO> vos = p.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        PageData<ShopOrderVO> pd = new PageData<>();
        pd.setTotal(p.getTotal());
        pd.setRecords(vos);
        return Result.success(pd);
    }

    @GetMapping("/{id}")
    public Result<ShopOrderVO> detail(@PathVariable Long id) {
        ShopOrder o = orderMapper.selectById(id);
        if (o==null||!o.getOwnerUserId().equals(ownerUserId)) return Result.error(404,"订单不存在");
        return Result.success(toVO(o));
    }

    @PatchMapping("/{id}/ship")
    public Result<Void> ship(@PathVariable Long id) {
        ShopOrder o = orderMapper.selectById(id);
        if (o==null||!o.getOwnerUserId().equals(ownerUserId)) return Result.error(404,"订单不存在");
        if (!"PENDING_SHIP".equals(o.getStatus())) return Result.error(400,"只有待发货订单可以发货");
        o.setStatus("SHIPPED"); o.setShipTime(LocalDateTime.now()); orderMapper.updateById(o);
        return Result.success("已发货",null);
    }

    @PatchMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id) {
        ShopOrder o = orderMapper.selectById(id);
        if (o==null||!o.getOwnerUserId().equals(ownerUserId)) return Result.error(404,"订单不存在");
        if (!"SHIPPED".equals(o.getStatus())) return Result.error(400,"只有已发货订单可以标记完成");
        o.setStatus("COMPLETED"); o.setCompleteTime(LocalDateTime.now()); orderMapper.updateById(o);
        return Result.success("已完成",null);
    }

    @PatchMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        ShopOrder o = orderMapper.selectById(id);
        if (o==null||!o.getOwnerUserId().equals(ownerUserId)) return Result.error(404,"订单不存在");
        if (!"PENDING_SHIP".equals(o.getStatus())) return Result.error(400,"只有待发货订单可以取消");
        List<ShopOrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<ShopOrderItem>().eq(ShopOrderItem::getOrderId,id));
        for (ShopOrderItem item : items) {
            Snack s = snackMapper.selectById(item.getSnackId());
            if (s!=null) {
                int oldQty = s.getQuantity(); s.setQuantity(oldQty+item.getQuantity()); snackMapper.updateById(s);
                StockRecord sr = new StockRecord();
                sr.setUserId(ownerUserId); sr.setSnackId(s.getId()); sr.setSnackName(s.getName());
                sr.setChangeType("IN"); sr.setChangeQty(item.getQuantity()); sr.setBeforeQty(oldQty); sr.setAfterQty(oldQty+item.getQuantity());
                sr.setRemark("管理端取消订单退回库存，订单号："+o.getOrderNo());
                stockRecordMapper.insert(sr);
            }
        }
        o.setStatus("CANCELLED"); o.setCancelTime(LocalDateTime.now()); orderMapper.updateById(o);
        return Result.success("已取消",null);
    }

    private ShopOrderVO toVO(ShopOrder o) {
        ShopOrderVO vo = new ShopOrderVO();
        vo.setId(o.getId()); vo.setOrderNo(o.getOrderNo()); vo.setStatus(o.getStatus());
        vo.setTotalAmount(o.getTotalAmount()); vo.setTotalQuantity(o.getTotalQuantity());
        vo.setReceiver(o.getReceiver()); vo.setPhone(o.getPhone()); vo.setAddress(o.getAddress());
        vo.setRemark(o.getRemark()); vo.setCreateTime(o.getCreateTime());
        vo.setShipTime(o.getShipTime()); vo.setCompleteTime(o.getCompleteTime()); vo.setCancelTime(o.getCancelTime());
        List<ShopOrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<ShopOrderItem>().eq(ShopOrderItem::getOrderId,o.getId()));
        vo.setItems(items.stream().map(i->{
            ShopOrderItemVO iv = new ShopOrderItemVO();
            iv.setId(i.getId()); iv.setSnackId(i.getSnackId()); iv.setSnackName(i.getSnackName());
            iv.setImageUrl(i.getImageUrl()); iv.setPrice(i.getPrice()); iv.setQuantity(i.getQuantity());
            iv.setUnit(i.getUnit()); iv.setSubtotal(i.getSubtotal());
            return iv;
        }).collect(Collectors.toList()));
        return vo;
    }
}
