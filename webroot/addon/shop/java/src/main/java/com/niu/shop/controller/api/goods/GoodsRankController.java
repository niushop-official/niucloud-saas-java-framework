package com.niu.shop.controller.api.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.admin.goods.param.ShopGoodsRankSearchParam;
import com.niu.shop.service.api.goods.IGoodsRankService;
import com.niu.shop.service.api.goods.param.GoodsRankComponentParam;
import com.niu.shop.service.api.goods.param.ShopGoodsRankGoodsPageParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/shop/rank")
public class GoodsRankController {
    private final IGoodsRankService goodsRankService;

    @GetMapping("/getRankConfig")
    Result<?> getRankConfig() {
        return Result.success(goodsRankService.getGoodsRankConfig());
    }

    @GetMapping
    Result<?> pages(ShopGoodsRankSearchParam searchParam) {
        return Result.success(goodsRankService.page(searchParam));
    }

    @GetMapping("/goods")
    Result<?> goods(PageParam pageParam, ShopGoodsRankGoodsPageParam param) {
        return Result.success(goodsRankService.getGoodsPage(pageParam, param));
    }

    @GetMapping("/components")
    Result<?> components(PageParam pageParam, GoodsRankComponentParam param) {
        return Result.success(goodsRankService.getRankComponents(pageParam, param));
    }

}
