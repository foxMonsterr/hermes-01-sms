package com.snack.controller.shop;
import com.snack.common.PageData;
import com.snack.common.Result;
import com.snack.service.ShopProductService;
import com.snack.vo.CategoryVO;
import com.snack.vo.ShopProductVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shop")
public class ShopProductController {
    private final ShopProductService shopProductService;
    private final Long ownerUserId;

    public ShopProductController(ShopProductService shopProductService,
            @Value("${shop.owner-user-id}") Long ownerUserId) {
        this.shopProductService = shopProductService; this.ownerUserId = ownerUserId;
    }

    @GetMapping("/products")
    public Result<PageData<ShopProductVO>> list(
            @RequestParam(defaultValue="1") int page,
            @RequestParam(defaultValue="12") int size,
            @RequestParam(required=false) String keyword,
            @RequestParam(required=false) Long categoryId,
            @RequestParam(defaultValue="default") String sort) {
        List<ShopProductVO> list = shopProductService.listProducts(ownerUserId, keyword, categoryId, sort, page, size);
        long total = shopProductService.countProducts(ownerUserId, keyword, categoryId);
        PageData<ShopProductVO> pd = new PageData<>();
        pd.setTotal(total);
        pd.setRecords(list);
        return Result.success(pd);
    }

    @GetMapping("/products/{id}")
    public Result<ShopProductVO> detail(@PathVariable Long id) {
        ShopProductVO vo = shopProductService.getProductDetail(ownerUserId, id);
        if (vo==null) return Result.error(404, "商品不存在");
        return Result.success(vo);
    }

    @GetMapping("/categories")
    public Result<List<CategoryVO>> categories() {
        return Result.success(shopProductService.listCategories(ownerUserId));
    }
}
