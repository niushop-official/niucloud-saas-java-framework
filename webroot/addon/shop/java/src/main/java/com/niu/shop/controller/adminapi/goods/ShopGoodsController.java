package com.niu.shop.controller.adminapi.goods;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import com.niu.shop.service.admin.goods.param.*;
import com.niu.shop.service.admin.goods.IShopGoodsService;
import com.niu.shop.service.admin.goods.vo.ShopGoodsInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.goods.vo.ShopGoodsSkuListVo;
import com.niu.shop.service.admin.goods.vo.ShopSelectGoodsListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 */

@RestController
@RequestMapping("adminapi/shop/goods")
public class ShopGoodsController {

    @Resource
    IShopGoodsService goodsService;

    /**
     * 商品列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopGoodsListVo>>
     */
    @GetMapping("")
    public Result<PageResult<ShopGoodsListVo>> list(@Validated PageParam pageParam, @Validated ShopGoodsSearchParam searchParam) {
        PageResult<ShopGoodsListVo> list = goodsService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品详情
     * @param id 主键ID
     * @return Result<ShopGoodsInfoVo>
     */
    @GetMapping("/{id}")
    public Result<ShopGoodsInfoVo> info(@PathVariable Integer id) {
        ShopGoodsInfoVo info = goodsService.info(id);
        return Result.success(info);
    }

    /**
     * 商品添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody ShopGoodsParam addParam) {
        goodsService.add(addParam);
        return Result.success();
    }

    /**
     * 商品编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable Integer id, @Validated @RequestBody ShopGoodsParam editParam) {
        goodsService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商品删除
     * @return Result<Object>
     */
    @PutMapping("/delete")
    public Result<Object> del(@Validated @RequestBody GetGoodsIdsParam param) {
        goodsService.del(param);
        return Result.success();
    }

    /**
     * 恢复商品
     * @return Result<Object>
     */
    @PutMapping("/recycle")
    public Result<Object> recycle(@Validated @RequestBody GetGoodsIdsParam param) {
        goodsService.recycle(param);
        return Result.success();
    }

    /**
     * 商品回收站
     * @return Result<Object>
     */
    @GetMapping("/recycle")
    public Result<PageResult<ShopGoodsListVo>> recyclePage(@Validated PageParam pageParam, @Validated ShopGoodsSearchParam searchParam) {
        return Result.success(goodsService.recyclePage(pageParam, searchParam));
    }

    /**
     * 修改商品上下架状态
     * @return Result<Object>
     */
    @PutMapping("/status")
    public Result<Object> editStatus(@Validated @RequestBody EditGoodsStatusParam param) {
        goodsService.editStatus(param);
        return Result.success();
    }

    /**
     * 修改商品排序号
     * @return Result<Object>
     */
    @PutMapping("/sort")
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
    public Result<Map<String, Map<String, Object>>> getGoodsType() {
        return Result.success(GoodsTypeEnum.getMap());
    }

    /**
     * 商品添加/编辑数据
     * @return
     */
    @GetMapping("/init")
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
    public Result<Object> copy(@PathVariable("goods_id") Integer goodsId) {
        goodsService.copy(goodsId);
        return Result.success();
    }

    /**
     * 查询商品参与营销活动的数量
     * @return
     */
    @GetMapping("/active/count")
    public Result<?> getActiveGoodsCount(@RequestParam(name = "goods_id") Integer goodsIds) {
        return Result.success(goodsService.getActiveGoodsCount(goodsIds));
    }

    /**
     * 获取商品选择分页列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopGoodsListVo>>
     */
    @GetMapping("/select")
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
     * 获取商品选择列表带ku
     * @return
     */
    @GetMapping("/selectgoodssku")
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
     * @return Result<List<ShopGoodsSku>>
     */
    @GetMapping("/sku")
    public Result<List<ShopGoodsSkuListVo>> getSkuList(@RequestParam("goods_id") Integer goodsId) {
        return Result.success(goodsService.getSkuList(goodsId));
    }

    /**
     * 编辑商品规格列表库存
     *
     * @return
     */
    @PutMapping("/sku/stock")
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
    public Result editGoodsListMemberPrice(@Validated @RequestBody EditGoodsListMemberPriceParam param) {
        goodsService.editGoodsListMemberPrice(param);
        return Result.success();
    }

}
