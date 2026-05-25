package com.snack.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.snack.common.BusinessException;
import com.snack.dto.ShopCartAddDTO;
import com.snack.entity.ShopCart;
import com.snack.entity.Snack;
import com.snack.mapper.ShopCartMapper;
import com.snack.mapper.SnackMapper;
import com.snack.service.ShopCartService;
import com.snack.vo.ShopCartItemVO;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShopCartServiceImpl implements ShopCartService {
    private final ShopCartMapper cartMapper;
    private final SnackMapper snackMapper;

    public ShopCartServiceImpl(ShopCartMapper cartMapper, SnackMapper snackMapper) {
        this.cartMapper = cartMapper; this.snackMapper = snackMapper;
    }

    @Override
    public List<ShopCartItemVO> getCart(Long userId, Long ownerUserId) {
        List<ShopCart> items = cartMapper.selectList(new LambdaQueryWrapper<ShopCart>().eq(ShopCart::getUserId, userId));
        List<ShopCartItemVO> vos = new ArrayList<>();
        for (ShopCart c : items) {
            Snack s = snackMapper.selectById(c.getSnackId());
            ShopCartItemVO vo = new ShopCartItemVO();
            vo.setId(c.getId()); vo.setSnackId(c.getSnackId()); vo.setQuantity(c.getQuantity());
            if (s!=null) {
                vo.setSnackName(s.getName()); vo.setImageUrl(s.getImageUrl());
                vo.setPrice(s.getPrice()); vo.setStock(s.getQuantity());
                vo.setOnShelf(s.getIsOnShelf()!=null&&s.getIsOnShelf()==1);
                if (s.getPrice()!=null) vo.setSubtotal(s.getPrice().multiply(BigDecimal.valueOf(c.getQuantity())));
            } else {
                vo.setOnShelf(false); vo.setStock(0);
            }
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public ShopCartItemVO addToCart(Long userId, Long ownerUserId, ShopCartAddDTO dto) {
        Snack s = snackMapper.selectOne(new LambdaQueryWrapper<Snack>()
            .eq(Snack::getId, dto.getSnackId()).eq(Snack::getUserId, ownerUserId).eq(Snack::getIsDeleted, 0));
        if (s==null||!Integer.valueOf(1).equals(s.getIsOnShelf())) throw BusinessException.badRequest("商品已下架或不存在");
        if (s.getQuantity()<=0) throw BusinessException.badRequest("商品已售罄");

        ShopCart exist = cartMapper.selectOne(new LambdaQueryWrapper<ShopCart>()
            .eq(ShopCart::getUserId, userId).eq(ShopCart::getSnackId, dto.getSnackId()));
        int newQty;
        if (exist!=null) {
            newQty = exist.getQuantity() + dto.getQuantity();
            if (newQty > s.getQuantity()) throw BusinessException.badRequest("库存不足，当前库存:"+s.getQuantity());
            exist.setQuantity(newQty); cartMapper.updateById(exist);
        } else {
            if (dto.getQuantity() > s.getQuantity()) throw BusinessException.badRequest("库存不足，当前库存:"+s.getQuantity());
            ShopCart c = new ShopCart(); c.setUserId(userId); c.setSnackId(dto.getSnackId()); c.setQuantity(dto.getQuantity());
            cartMapper.insert(c);
            newQty = dto.getQuantity();
        }
        ShopCartItemVO vo = new ShopCartItemVO();
        vo.setSnackId(s.getId()); vo.setSnackName(s.getName()); vo.setImageUrl(s.getImageUrl());
        vo.setPrice(s.getPrice()); vo.setStock(s.getQuantity()); vo.setQuantity(newQty);
        vo.setOnShelf(true);
        if (s.getPrice()!=null) vo.setSubtotal(s.getPrice().multiply(BigDecimal.valueOf(newQty)));
        return vo;
    }

    @Override
    public void updateQuantity(Long userId, Long cartId, Integer quantity, Long ownerUserId) {
        ShopCart c = cartMapper.selectById(cartId);
        if (c==null||!c.getUserId().equals(userId)) throw BusinessException.badRequest("购物车项不存在");
        Snack s = snackMapper.selectOne(new LambdaQueryWrapper<Snack>()
            .eq(Snack::getId, c.getSnackId()).eq(Snack::getUserId, ownerUserId).eq(Snack::getIsDeleted, 0));
        if (s!=null&&quantity>s.getQuantity()) throw BusinessException.badRequest("库存不足");
        c.setQuantity(quantity); cartMapper.updateById(c);
    }

    @Override
    public void deleteItem(Long userId, Long cartId) {
        ShopCart c = cartMapper.selectById(cartId);
        if (c==null||!c.getUserId().equals(userId)) throw BusinessException.badRequest("购物车项不存在");
        cartMapper.deleteById(cartId);
    }

    @Override
    public void clearCart(Long userId) {
        cartMapper.delete(new LambdaQueryWrapper<ShopCart>().eq(ShopCart::getUserId, userId));
    }
}
