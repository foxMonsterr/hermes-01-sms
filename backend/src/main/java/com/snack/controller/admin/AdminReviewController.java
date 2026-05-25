package com.snack.controller.admin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.snack.common.PageData;
import com.snack.common.Result;
import com.snack.entity.ShopReview;
import com.snack.mapper.ShopReviewMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reviews")
public class AdminReviewController {
    private final ShopReviewMapper reviewMapper;
    public AdminReviewController(ShopReviewMapper reviewMapper) { this.reviewMapper = reviewMapper; }

    @GetMapping
    public Result<PageData<ShopReview>> list(@RequestParam(defaultValue="1") int page,
            @RequestParam(defaultValue="10") int size) {
        Page<ShopReview> p = reviewMapper.selectPage(new Page<>(page,size),
            new LambdaQueryWrapper<ShopReview>().orderByDesc(ShopReview::getCreateTime));
        PageData<ShopReview> pd = new PageData<>();
        pd.setTotal(p.getTotal()); pd.setRecords(p.getRecords());
        return Result.success(pd);
    }

    @PatchMapping("/{id}/hide")
    public Result<Void> toggleHide(@PathVariable Long id) {
        ShopReview r = reviewMapper.selectById(id);
        if (r==null) return Result.error(404,"评价不存在");
        r.setIsHidden(r.getIsHidden()!=null&&r.getIsHidden()==1?0:1);
        reviewMapper.updateById(r);
        return Result.success("操作成功", null);
    }
}
