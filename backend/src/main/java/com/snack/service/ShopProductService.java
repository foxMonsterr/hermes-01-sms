package com.snack.service;
import com.snack.vo.ShopProductVO;
import com.snack.vo.CategoryVO;
import java.util.List;

public interface ShopProductService {
    List<ShopProductVO> listProducts(Long ownerUserId, String keyword, Long categoryId, String sort, int page, int size);
    long countProducts(Long ownerUserId, String keyword, Long categoryId);
    ShopProductVO getProductDetail(Long ownerUserId, Long snackId);
    List<CategoryVO> listCategories(Long ownerUserId);
}
