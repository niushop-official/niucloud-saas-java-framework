package com.niu.shop.controller.api.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.api.goods.param.ShopGoodsComponentsSearchParam;
import com.niu.shop.service.api.goods.param.ShopGoodsDetailParam;
import com.niu.shop.service.api.goods.param.ShopGoodsSearchParam;
import com.niu.shop.service.api.goods.vo.ShopGoodsDetailVo;
import com.niu.shop.service.api.goods.vo.ShopGoodsListVo;
import com.niu.shop.service.api.goods.IGoodsService;
import com.niu.shop.service.api.goods.vo.ShopGoodsSkuVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/shop/goods")
public class GoodsController {

    @Resource
    IGoodsService goodsService;

    /**
     * 获取商品列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("/pages")
    public Result<PageResult<ShopGoodsListVo>> page(@Validated PageParam pageParam, @Validated ShopGoodsSearchParam searchParam) {
        PageResult<ShopGoodsListVo> list = goodsService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 获取商品详情
     *
     * @param param
     * @return
     */
    @GetMapping("/detail")
    public Result<ShopGoodsDetailVo> detail(@Validated ShopGoodsDetailParam param) {
        return Result.success(goodsService.detail(param));
    }

    /**
     * 获取商品规格信息，切换规格
     *
     * @return
     */
    @GetMapping("/sku/{sku_id}")
    public Result<ShopGoodsSkuVo> sku(@PathVariable("sku_id") Integer skuId) {
        return Result.success(goodsService.getSku(skuId));
    }

    /**
     * 获取商品列表供组件调用
     *
     * @return
     */
    @GetMapping("/components")
    public Result components(@RequestParam(value = "goods_ids[]", required = false) Integer[] goodsIds, ShopGoodsComponentsSearchParam param) {
        param.setGoodsIds(goodsIds);
        return Result.success(goodsService.getGoodsComponents(param));
    }
}
