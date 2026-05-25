package com.snack.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.snack.common.BusinessException;
import com.snack.dto.ShopOrderCreateDTO;
import com.snack.entity.*;
import com.snack.mapper.*;
import com.snack.service.ShopOrderService;
import com.snack.vo.ShopOrderItemVO;
import com.snack.vo.ShopOrderVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShopOrderServiceImpl implements ShopOrderService {
    private final ShopOrderMapper orderMapper;
    private final ShopOrderItemMapper orderItemMapper;
    private final ShopCartMapper cartMapper;
    private final SnackMapper snackMapper;
    private final StockRecordMapper stockRecordMapper;

    public ShopOrderServiceImpl(ShopOrderMapper orderMapper, ShopOrderItemMapper orderItemMapper,
            ShopCartMapper cartMapper, SnackMapper snackMapper, StockRecordMapper stockRecordMapper) {
        this.orderMapper=orderMapper; this.orderItemMapper=orderItemMapper;
        this.cartMapper=cartMapper; this.snackMapper=snackMapper; this.stockRecordMapper=stockRecordMapper;
    }

    @Override
    @Transactional
    public ShopOrderVO createOrder(Long userId, Long ownerUserId, ShopOrderCreateDTO dto) {
        // 1. 查询购物车项
        List<ShopCart> cartItems = cartMapper.selectList(new LambdaQueryWrapper<ShopCart>()
            .eq(ShopCart::getUserId, userId).in(ShopCart::getId, dto.getCartItemIds()));
        if (cartItems.isEmpty()) throw BusinessException.badRequest("购物车为空");

        // 2. 校验商品 + 计算金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalQty = 0;
        List<Snack> snacks = new ArrayList<>();
        List<Integer> buyQtys = new ArrayList<>();

        for (ShopCart c : cartItems) {
            Snack s = snackMapper.selectOne(new LambdaQueryWrapper<Snack>()
                .eq(Snack::getId, c.getSnackId()).eq(Snack::getUserId, ownerUserId).eq(Snack::getIsDeleted, 0));
            if (s==null||!Integer.valueOf(1).equals(s.getIsOnShelf())) throw BusinessException.badRequest("商品"+(s!=null?s.getName():"未知")+"已下架");
            if (s.getPrice()==null) throw BusinessException.badRequest("商品"+s.getName()+"价格异常");
            if (c.getQuantity()>s.getQuantity()) throw BusinessException.badRequest("商品"+s.getName()+"库存不足，当前库存:"+s.getQuantity());
            snacks.add(s); buyQtys.add(c.getQuantity());
            totalAmount = totalAmount.add(s.getPrice().multiply(BigDecimal.valueOf(c.getQuantity())));
            totalQty += c.getQuantity();
        }

        // 3. 生成订单号
        String orderNo = "SN"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))+String.format("%04d",(int)(Math.random()*10000));

        // 4. 创建订单
        ShopOrder order = new ShopOrder();
        order.setOrderNo(orderNo); order.setShopUserId(userId); order.setOwnerUserId(ownerUserId);
        order.setStatus("PENDING_SHIP"); order.setTotalAmount(totalAmount); order.setTotalQuantity(totalQty);
        order.setReceiver(dto.getReceiver()); order.setPhone(dto.getPhone()); order.setAddress(dto.getAddress());
        order.setRemark(dto.getRemark()); order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);

        // 5. 创建订单明细 + 扣库存 + 生成流水
        for (int i=0;i<snacks.size();i++) {
            Snack s = snacks.get(i); int qty = buyQtys.get(i);

            ShopOrderItem item = new ShopOrderItem();
            item.setOrderId(order.getId()); item.setSnackId(s.getId()); item.setSnackName(s.getName());
            item.setImageUrl(s.getImageUrl()); item.setPrice(s.getPrice()); item.setQuantity(qty);
            item.setUnit(s.getUnit()); item.setSubtotal(s.getPrice().multiply(BigDecimal.valueOf(qty)));
            orderItemMapper.insert(item);

            int oldQty = s.getQuantity();
            s.setQuantity(oldQty - qty);
            snackMapper.updateById(s);

            StockRecord sr = new StockRecord();
            sr.setUserId(ownerUserId); sr.setSnackId(s.getId()); sr.setSnackName(s.getName());
            sr.setChangeType("OUT"); sr.setChangeQty(-qty);
            sr.setBeforeQty(oldQty); sr.setAfterQty(oldQty - qty);
            sr.setRemark("订单销售出库，订单号："+orderNo);
            stockRecordMapper.insert(sr);
        }

        // 6. 清空已购购物车项
        List<Long> ids = cartItems.stream().map(ShopCart::getId).collect(Collectors.toList());
        cartMapper.deleteBatchIds(ids);

        return toVO(order);
    }

    @Override
    public List<ShopOrderVO> getMyOrders(Long userId, int page, int size, String status) {
        var q = new LambdaQueryWrapper<ShopOrder>()
            .eq(ShopOrder::getShopUserId, userId).eq(ShopOrder::getIsDeleted, 0)
            .orderByDesc(ShopOrder::getCreateTime);
        if (status!=null&&!status.isBlank()) q.eq(ShopOrder::getStatus, status);
        return orderMapper.selectPage(new Page<>(page,size), q).getRecords().stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public long countMyOrders(Long userId, String status) {
        var q = new LambdaQueryWrapper<ShopOrder>().eq(ShopOrder::getShopUserId, userId).eq(ShopOrder::getIsDeleted, 0);
        if (status!=null&&!status.isBlank()) q.eq(ShopOrder::getStatus, status);
        return orderMapper.selectCount(q);
    }

    @Override
    public ShopOrderVO getOrderDetail(Long userId, Long orderId) {
        ShopOrder o = orderMapper.selectById(orderId);
        if (o==null||!o.getShopUserId().equals(userId)) throw BusinessException.notFound("订单不存在");
        return toVO(o);
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId, Long ownerUserId) {
        ShopOrder o = orderMapper.selectById(orderId);
        if (o==null||!o.getShopUserId().equals(userId)) throw BusinessException.notFound("订单不存在");
        if (!"PENDING_SHIP".equals(o.getStatus())) throw BusinessException.badRequest("只有待发货订单可以取消");

        // 退库存
        List<ShopOrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<ShopOrderItem>().eq(ShopOrderItem::getOrderId, orderId));
        for (ShopOrderItem item : items) {
            Snack s = snackMapper.selectById(item.getSnackId());
            if (s!=null) {
                int oldQty = s.getQuantity();
                s.setQuantity(oldQty + item.getQuantity());
                snackMapper.updateById(s);

                StockRecord sr = new StockRecord();
                sr.setUserId(ownerUserId); sr.setSnackId(s.getId()); sr.setSnackName(s.getName());
                sr.setChangeType("IN"); sr.setChangeQty(item.getQuantity());
                sr.setBeforeQty(oldQty); sr.setAfterQty(oldQty+item.getQuantity());
                sr.setRemark("订单取消退回库存，订单号："+o.getOrderNo());
                stockRecordMapper.insert(sr);
            }
        }
        o.setStatus("CANCELLED"); o.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(o);
    }

    @Override
    public void confirmReceive(Long userId, Long orderId) {
        ShopOrder o = orderMapper.selectById(orderId);
        if (o==null||!o.getShopUserId().equals(userId)) throw BusinessException.notFound("订单不存在");
        if (!"SHIPPED".equals(o.getStatus())) throw BusinessException.badRequest("只有已发货订单可以确认收货");
        o.setStatus("COMPLETED"); o.setCompleteTime(LocalDateTime.now());
        orderMapper.updateById(o);
    }

    private ShopOrderVO toVO(ShopOrder o) {
        ShopOrderVO vo = new ShopOrderVO();
        vo.setId(o.getId()); vo.setOrderNo(o.getOrderNo()); vo.setStatus(o.getStatus());
        vo.setTotalAmount(o.getTotalAmount()); vo.setTotalQuantity(o.getTotalQuantity());
        vo.setReceiver(o.getReceiver()); vo.setPhone(o.getPhone()); vo.setAddress(o.getAddress());
        vo.setRemark(o.getRemark()); vo.setCreateTime(o.getCreateTime());
        vo.setShipTime(o.getShipTime()); vo.setCompleteTime(o.getCompleteTime()); vo.setCancelTime(o.getCancelTime());

        List<ShopOrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<ShopOrderItem>().eq(ShopOrderItem::getOrderId, o.getId()));
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
