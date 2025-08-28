package com.niu.shop.controller.adminapi.goods;

import com.niu.shop.enums.goods.GoodsBatchSetEnum;
import jdk.jfr.Description;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import com.niu.shop.service.admin.goods.IShopGoodsService;
import com.niu.shop.service.admin.goods.param.*;
import com.niu.shop.service.admin.goods.vo.ShopGoodsInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsListVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsSkuListVo;
import com.niu.shop.service.admin.goods.vo.ShopSelectGoodsListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 */

@RestController
@RequestMapping("adminapi/shop/goods")
@Description("商品管理")
public class ShopGoodsController {

    @Resource
    IShopGoodsService goodsService;

    /**
     * 商品列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < ShopGoodsListVo>>
     */
    @GetMapping("")
    @Description("获取商品分页列表")
    public Result<PageResult<ShopGoodsListVo>> list(@Validated PageParam pageParam, @Validated ShopGoodsSearchParam searchParam) {
        PageResult<ShopGoodsListVo> list = goodsService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品详情
     *
     * @param id 主键ID
     * @return Result<ShopGoodsInfoVo>
     */
    @GetMapping("/{id}")
    @Description("获取商品详情")
    public Result<ShopGoodsInfoVo> info(@PathVariable("id") Integer id) {
        ShopGoodsInfoVo info = goodsService.info(id);
        return Result.success(info);
    }

    /**
     * 商品添加
     *
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    @Description("添加商品")
    public Result<Object> add(@Validated @RequestBody ShopGoodsParam addParam) {
        goodsService.add(addParam);
        return Result.success();
    }

    /**
     * 商品编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    @Description("编辑商品")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody ShopGoodsParam editParam) {
        goodsService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商品删除
     *
     * @return Result<Object>
     */
    @PutMapping("/delete")
    @Description("删除商品")
    public Result<Object> del(@Validated @RequestBody GetGoodsIdsParam param) {
        goodsService.del(param);
        return Result.success();
    }

    /**
     * 恢复商品
     *
     * @return Result<Object>
     */
    @PutMapping("/recycle")
    @Description("恢复商品")
    public Result<Object> recycle(@Validated @RequestBody GetGoodsIdsParam param) {
        goodsService.recycle(param);
        return Result.success();
    }

    /**
     * 商品回收站
     *
     * @return Result<Object>
     */
    @GetMapping("/recycle")
    @Description("获取商品回收站列表")
    public Result<PageResult<ShopGoodsListVo>> recyclePage(@Validated PageParam pageParam, @Validated ShopGoodsSearchParam searchParam) {
        return Result.success(goodsService.recyclePage(pageParam, searchParam));
    }

    /**
     * 修改商品上下架状态
     *
     * @return Result<Object>
     */
    @PutMapping("/status")
    @Description("修改商品上下架状态")
    public Result<Object> editStatus(@Validated @RequestBody EditGoodsStatusParam param) {
        goodsService.editStatus(param);
        return Result.success();
    }

    @PutMapping("/single/status")
    @Description("修改商品上下架状态(单商品)")
    public Result<Object> editSingleStatus(@Validated @RequestBody EditGoodsSimpleStatusParam param) {
        goodsService.editSimpleStatus(param);
        return Result.success();
    }

    /**
     * 修改商品排序号
     *
     * @return Result<Object>
     */
    @PutMapping("/sort")
    @Description("修改商品排序")
    public Result<Object> editSort(@Validated @RequestBody EditGoodsSortParam param) {
        goodsService.editSort(param);
        return Result.success();
    }

    /**
     * 获取商品类型
     *
     * @return
     */
    @GetMapping("/type")
    @Description("获取商品类型")
    public Result<Map<String, Map<String, Object>>> getGoodsType() {
        return Result.success(GoodsTypeEnum.getMap());
    }

    /**
     * 商品添加/编辑数据
     *
     * @return
     */
    @GetMapping("/init")
    @Description("获取商品初始化数据")
    public Result<?> init(@RequestParam(value = "goods_id", defaultValue = "0") Integer goodsId) {
        return Result.success(goodsService.getInit(goodsId));
    }

    /**
     * 复制商品
     *
     * @param goodsId
     * @return
     */
    @PutMapping("/copy/{goods_id}")
    @Description("复制商品")
    public Result<Object> copy(@PathVariable("goods_id") Integer goodsId) {
        goodsService.copy(goodsId);
        return Result.success();
    }

    /**
     * 查询商品参与营销活动的数量
     *
     * @return
     */
    @GetMapping("/active/count")
    @Description("查询商品参与营销活动数量")
    public Result<?> getActiveGoodsCount(@RequestParam(name = "goods_id") Integer goodsIds) {
        return Result.success(goodsService.getActiveGoodsCount(goodsIds));
    }

    /**
     * 获取商品选择分页列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < ShopGoodsListVo>>
     */
    @GetMapping("/select")
    @Description("获取商品选择分页列表")
    public Result<ShopSelectGoodsListVo> getSelectPage(
            @Validated PageParam pageParam,
            @Validated ShopGoodsSelectSearchParam searchParam,
            @RequestParam(name = "goods_ids[]", required = false) List<Integer> goodsIds,
            @RequestParam(name = "sku_ids[]", required = false) List<Integer> skuIds,
            @RequestParam(name = "verify_goods_ids[]", required = false) List<Integer> verifyGoodsIds,
            @RequestParam(name = "verify_sku_ids[]", required = false) List<Integer> verifySkuIds) {
        searchParam.setGoodsIds(goodsIds);
        searchParam.setSkuIds(skuIds);
        searchParam.setVerifyGoodsIds(verifyGoodsIds);
        searchParam.setVerifySkuIds(verifySkuIds);
        ShopSelectGoodsListVo list = goodsService.getSelectPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 获取商品选择列表带sku
     *
     * @return
     */
    @GetMapping("/selectgoodssku")
    @Description("获取商品选择列表带sku")
    public Result getSelectGoodsSku(@Validated ShopGoodsSelectSearchParam searchParam,
                                    @RequestParam(name = "goods_ids[]", required = false) List<Integer> goodsIds,
                                    @RequestParam(name = "sku_ids[]", required = false) List<Integer> skuIds,
                                    @RequestParam(name = "verify_goods_ids[]", required = false) List<Integer> verifyGoodsIds,
                                    @RequestParam(name = "verify_sku_ids[]", required = false) List<Integer> verifySkuIds) {
        searchParam.setGoodsIds(goodsIds);
        searchParam.setSkuIds(skuIds);
        searchParam.setVerifyGoodsIds(verifyGoodsIds);
        searchParam.setVerifySkuIds(verifySkuIds);
        return Result.success(goodsService.getSelectGoodsSku(searchParam));
    }

    /**
     * 查询商品SKU规格列表
     *
     * @return Result<List < ShopGoodsSku>>
     */
    @GetMapping("/sku")
    @Description("查询商品SKU规格列表")
    public Result<List<ShopGoodsSkuListVo>> getSkuList(@RequestParam("goods_id") Integer goodsId) {
        return Result.success(goodsService.getSkuList(goodsId));
    }

    /**
     * 编辑商品规格列表库存
     *
     * @return
     */
    @PutMapping("/sku/stock")
    @Description("编辑商品规格列表库存")
    public Result editGoodsListStock(@Validated @RequestBody EditGoodsListStockParam param) {
        goodsService.editGoodsListStock(param);
        return Result.success();
    }

    /**
     * 编辑商品规格列表价格
     *
     * @return
     */
    @PutMapping("/sku/price")
    @Description("编辑商品规格列表价格")
    public Result editGoodsListPrice(@Validated @RequestBody EditGoodsListPriceParam param) {
        goodsService.editGoodsListPrice(param);
        return Result.success();
    }

    /**
     * 编辑商品规格列表会员价
     *
     * @return
     */
    @PutMapping("/sku/member_price")
    @Description("编辑商品规格列表会员价")
    public Result editGoodsListMemberPrice(@Validated @RequestBody EditGoodsListMemberPriceParam param) {
        goodsService.editGoodsListMemberPrice(param);
        return Result.success();
    }

    @GetMapping("/buy/goods/select")
    @Description("获取购买商品选择列表")
    public Result<?> buyGoodsSelect(PageParam pageParam, BuyGoodsSelectParam param) {
        return Result.success(goodsService.getBuyGoodsSelect(pageParam, param));
    }

    @GetMapping("/buy/sku/select")
    @Description("获取购买SKU选择列表")
    public Result<?> buySkuSelect(BuySkuSelectParam param) {
        return Result.success(goodsService.getBuySkuSelect(param));
    }

    @PostMapping("/buy/goods/selected")
    @Description("获取已选购买商品")
    public Result<?> buyGoodsSelected(BuyGoodsSelectedParam param) {
        return Result.success(goodsService.getBuyGoodsSelected(param));
    }

    /**
     * 获取批量设置类型
     */
    @GetMapping("/batchSet/dict")
    @Description("获取批量设置类型")
    public Result<?> getBatchSetDict() {
        return Result.success(GoodsBatchSetEnum.getMap());
    }

    /**
     * 批量设置商品
     */
    @PutMapping("/batchSet")
    @Description("批量设置商品")
    public Result<?> batchSet(@Validated @RequestBody ShopGoodsBatchSetParam param) {
        goodsService.batchSet(param);
        return Result.success();
    }

    @Description("验证商品编码是否重复")
    @PostMapping("/verify/skuno")
    public Result verifySkuNo(@RequestBody ShopGoodsVerifySkuNoParam param) {
        if(goodsService.verifySkuNo(param)){
            return Result.success();
        }else{
            return Result.success();
        }
    }

}
