package com.niu.shop.controller.adminapi.goods;

import jdk.jfr.Description;
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
@Description("商品统计管理")
public class ShopGoodsStatController {
    private final IShopGoodsStatService shopGoodsStatService;

    @GetMapping("/basic")
    @Description("获取商品基础统计数据")
    Result<?> basic(ShopGoodsStatDateParam param) {
        return Result.success(shopGoodsStatService.getBasic(param));
    }

    @GetMapping("/trend")
    @Description("获取商品趋势统计数据")
    Result<?> trend(ShopGoodsStatDateParam param) {
        return Result.success(shopGoodsStatService.getTrend(param));
    }

    @GetMapping("/rank")
    @Description("获取商品排名统计数据")
    Result<?> rank(PageParam pageParam, GoodsStatGetRankParam param) {
        return Result.success(shopGoodsStatService.getRank(pageParam, param));
    }

    @GetMapping("/type")
    @Description("获取商品类型统计数据")
    Result<?> type() {
        return Result.success(shopGoodsStatService.getType());
    }


}
