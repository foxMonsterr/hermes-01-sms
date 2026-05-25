package com.snack.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.snack.entity.Category;
import com.snack.entity.Snack;
import com.snack.mapper.CategoryMapper;
import com.snack.mapper.SnackMapper;
import com.snack.service.ShopProductService;
import com.snack.vo.CategoryVO;
import com.snack.vo.ShopProductVO;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShopProductServiceImpl implements ShopProductService {
    private final SnackMapper snackMapper;
    private final CategoryMapper categoryMapper;

    public ShopProductServiceImpl(SnackMapper snackMapper, CategoryMapper categoryMapper) {
        this.snackMapper = snackMapper; this.categoryMapper = categoryMapper;
    }

    @Override
    public List<ShopProductVO> listProducts(Long ownerUserId, String keyword, Long categoryId, String sort, int page, int size) {
        var q = new LambdaQueryWrapper<Snack>()
            .eq(Snack::getUserId, ownerUserId)
            .eq(Snack::getIsOnShelf, 1)
            .eq(Snack::getIsDeleted, 0);
        if (keyword!=null&&!keyword.isBlank()) q.like(Snack::getName, keyword);
        if (categoryId!=null) q.eq(Snack::getCategoryId, categoryId);
        if ("price_asc".equals(sort)) q.orderByAsc(Snack::getPrice);
        else if ("price_desc".equals(sort)) q.orderByDesc(Snack::getPrice);
        else q.orderByDesc(Snack::getShelfTime);

        Page<Snack> p = snackMapper.selectPage(new Page<>(page, size), q);
        return p.getRecords().stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public long countProducts(Long ownerUserId, String keyword, Long categoryId) {
        var q = new LambdaQueryWrapper<Snack>()
            .eq(Snack::getUserId, ownerUserId)
            .eq(Snack::getIsOnShelf, 1)
            .eq(Snack::getIsDeleted, 0);
        if (keyword!=null&&!keyword.isBlank()) q.like(Snack::getName, keyword);
        if (categoryId!=null) q.eq(Snack::getCategoryId, categoryId);
        return snackMapper.selectCount(q);
    }

    @Override
    public ShopProductVO getProductDetail(Long ownerUserId, Long snackId) {
        Snack s = snackMapper.selectOne(new LambdaQueryWrapper<Snack>()
            .eq(Snack::getId, snackId).eq(Snack::getUserId, ownerUserId).eq(Snack::getIsDeleted, 0));
        if (s==null) return null;
        return toVO(s);
    }

    @Override
    public List<CategoryVO> listCategories(Long ownerUserId) {
        // 只返回有上架商品的分类
        List<Snack> snacks = snackMapper.selectList(new LambdaQueryWrapper<Snack>()
            .eq(Snack::getUserId, ownerUserId).eq(Snack::getIsOnShelf, 1).eq(Snack::getIsDeleted, 0)
            .select(Snack::getCategoryId));
        var catIds = snacks.stream().map(Snack::getCategoryId).distinct().collect(Collectors.toSet());
        if (catIds.isEmpty()) return List.of();
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
            .in(Category::getId, catIds).eq(Category::getIsDeleted, 0))
            .stream().map(c -> new CategoryVO(c.getId(), c.getName(), c.getIcon(), c.getSortOrder()))
            .collect(Collectors.toList());
    }

    private ShopProductVO toVO(Snack s) {
        String catName = "";
        if (s.getCategoryId()!=null) {
            var c = categoryMapper.selectById(s.getCategoryId());
            if (c!=null) catName = c.getName();
        }
        String expiryStatus = "normal";
        if (s.getExpiryDate()!=null) {
            long days = ChronoUnit.DAYS.between(LocalDate.now(), s.getExpiryDate());
            if (days<0) expiryStatus = "expired"; else if (days<=3) expiryStatus = "soon"; else if (days<=7) expiryStatus = "warning";
        }
        return new ShopProductVO(s.getId(),s.getName(),s.getCategoryId(),catName,
            s.getQuantity(),s.getUnit(),s.getPrice(),s.getImageUrl(),s.getDescription(),
            expiryStatus, s.getQuantity()>0);
    }
}
