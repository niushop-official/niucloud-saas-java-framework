package com.niu.shop.controller.adminapi.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.admin.goods.IShopGoodsStatService;
import com.niu.shop.service.admin.goods.param.GoodsStatGetRankParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsStatDateParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("adminapi/shop/goods/statistics")
public class ShopGoodsStatController {
    private final IShopGoodsStatService shopGoodsStatService;

    @GetMapping("/basic")
    Result<?> basic(ShopGoodsStatDateParam param) {
        return Result.success(shopGoodsStatService.getBasic(param));
    }

    @GetMapping("/trend")
    Result<?> trend(ShopGoodsStatDateParam param) {
        return Result.success(shopGoodsStatService.getTrend(param));
    }

    @GetMapping("/rank")
    Result<?> rank(PageParam pageParam, GoodsStatGetRankParam param) {
        return Result.success(shopGoodsStatService.getRank(pageParam, param));
    }

    @GetMapping("/type")
    Result<?> type() {
        return Result.success(shopGoodsStatService.getType());
    }


}
