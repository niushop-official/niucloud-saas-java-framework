package com.niu.shop.controller.adminapi.goods;

import jdk.jfr.Description;
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
@Description("商品排行榜管理")
public class ShopGoodsRankController {

    private final IShopGoodsRankService ShopGoodsRankService;

    @PostMapping("/good/rank/config")
    @Description("设置商品排行榜配置")
    Result<?> setRankConfig(@RequestBody ShopGoodsRankConfigParam param) {
        ShopGoodsRankService.setGoodsRankConfig(param);
        return Result.success();
    }

    @GetMapping("/good/rank/config")
    @Description("获取商品排行榜配置")
    Result<?> getRankConfig() {
        return Result.success(ShopGoodsRankService.getGoodsRankConfig());
    }

    @GetMapping("/good/rank")
    @Description("获取商品排行榜分页列表")
    Result<?> page(@Validated PageParam pageParam, @Validated ShopGoodsRankSearchParam searchParam) {
        return Result.success(ShopGoodsRankService.page(pageParam, searchParam));
    }

    @PostMapping("/good/rank")
    @Description("添加商品排行榜")
    Result<?> add(@Validated @RequestBody ShopGoodsRankParam addParam) {
        ShopGoodsRankService.add(addParam);
        return Result.success();
    }

    @PutMapping("/good/rank/{id}")
    @Description("编辑商品排行榜")
    Result<?> edit(@PathVariable("id") Integer id, @Validated @RequestBody ShopGoodsRankParam editParam) {
        ShopGoodsRankService.edit(id, editParam);
        return Result.success();
    }

    @GetMapping("/good/rank/{id}")
    @Description("获取商品排行榜详情")
    Result<?> info(@PathVariable("id") Integer id) {
        ShopGoodsRankInfoVo info = ShopGoodsRankService.info(id);
        return Result.success(info);
    }

    @DeleteMapping("/good/rank/{id}")
    @Description("删除商品排行榜")
    Result<Object> del(@PathVariable("id") Integer id) {
        ShopGoodsRankService.del(id);
        return Result.success();
    }

    @GetMapping("/good/rank/dict")
    @Description("获取商品排行榜选项数据")
    Result<?> getOptionData() {
        return Result.success(ShopGoodsRankService.getOptionData());
    }

    @PutMapping("/good/rank/sort")
    @Description("修改商品排行榜排序")
    Result<?> editSort(@RequestBody ShopGoodsRankEditSortParam param) {
        ShopGoodsRankService.editSort(param);
        return Result.success();
    }

    @PutMapping("/good/rank/batchDelete")
    @Description("批量删除商品排行榜")
    Result<?> batchDelete(@RequestBody ShopGoodsRankBatchDeleteParam param) {
        ShopGoodsRankService.batchDelete(param);
        return Result.success();
    }

    @GetMapping("/good/rank/select")
    @Description("获取商品排行榜选择列表")
    Result<?> select(PageParam pageParam, ShopGoodsRankSelectPageParam param) {
        return Result.success(ShopGoodsRankService.getSelectPage(pageParam, param));
    }

    @PutMapping("/goods/rank/status")
    @Description("修改商品排行榜状态")
    Result<?> modifyStatus(@RequestBody ShopGoodsRankModifyStatusParam param) {
        ShopGoodsRankService.modifyStatus(param);
        return Result.success();
    }

}
