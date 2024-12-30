package com.niu.shop.controller.adminapi.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.admin.goods.IShopGoodsRankService;
import com.niu.shop.service.admin.goods.param.*;
import com.niu.shop.service.admin.goods.vo.ShopGoodsRankInfoVo;
import com.niu.shop.service.core.goods.param.ShopGoodsRankConfigParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商品排行榜控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("adminapi/shop")
class ShopGoodsRankController {

    private final IShopGoodsRankService ShopGoodsRankService;

    @PostMapping("/good/rank/config")
    Result<?> setRankConfig(@RequestBody ShopGoodsRankConfigParam param) {
        ShopGoodsRankService.setGoodsRankConfig(param);
        return Result.success();
    }

    @GetMapping("/good/rank/config")
    Result<?> getRankConfig() {
        return Result.success(ShopGoodsRankService.getGoodsRankConfig());
    }

    @GetMapping("/good/rank")
    Result<?> page(@Validated PageParam pageParam, @Validated ShopGoodsRankSearchParam searchParam) {
        return Result.success(ShopGoodsRankService.page(pageParam, searchParam));
    }

    @PostMapping("/good/rank")
    Result<?> add(@Validated @RequestBody ShopGoodsRankParam addParam) {
        ShopGoodsRankService.add(addParam);
        return Result.success();
    }

    @PutMapping("/good/rank/{id}")
    Result<?> edit(@PathVariable Integer id, @Validated @RequestBody ShopGoodsRankParam editParam) {
        ShopGoodsRankService.edit(id, editParam);
        return Result.success();
    }

    @GetMapping("/good/rank/{id}")
    Result<?> info(@PathVariable Integer id) {
        ShopGoodsRankInfoVo info = ShopGoodsRankService.info(id);
        return Result.success(info);
    }

    @DeleteMapping("/good/rank/{id}")
    Result<Object> del(@PathVariable Integer id) {
        ShopGoodsRankService.del(id);
        return Result.success();
    }

    @GetMapping("/good/rank/dict")
    Result<?> getOptionData() {
        return Result.success(ShopGoodsRankService.getOptionData());
    }

    @PutMapping("/good/rank/sort")
    Result<?> editSort(@RequestBody ShopGoodsRankEditSortParam param) {
        ShopGoodsRankService.editSort(param);
        return Result.success();
    }

    @PutMapping("/good/rank/batchDelete")
    Result<?> batchDelete(@RequestBody ShopGoodsRankBatchDeleteParam param) {
        ShopGoodsRankService.batchDelete(param);
        return Result.success();
    }

    @GetMapping("/good/rank/select")
    Result<?> select(PageParam pageParam, ShopGoodsRankSelectPageParam param) {
        return Result.success(ShopGoodsRankService.getSelectPage(pageParam, param));
    }

    @PutMapping("/goods/rank/status")
    Result<?> modifyStatus(@RequestBody ShopGoodsRankModifyStatusParam param) {
        ShopGoodsRankService.modifyStatus(param);
        return Result.success();
    }

}
