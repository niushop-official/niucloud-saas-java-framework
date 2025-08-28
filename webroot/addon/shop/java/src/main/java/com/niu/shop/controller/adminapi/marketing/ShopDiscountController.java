package com.niu.shop.controller.adminapi.marketing;

import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.service.admin.marketing.IShopDiscountService;
import com.niu.shop.service.admin.marketing.param.*;
import com.niu.shop.service.admin.marketing.vo.*;
import jakarta.annotation.Resource;
import jdk.jfr.Description;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 限时折扣控制器
 */

@RestController
@RequestMapping("adminapi/shop/active")
@Description("限时折扣管理")
public class ShopDiscountController {

    @Resource
    IShopDiscountService shopDiscountService;

    /**
     * 限时折扣列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < ShopActiveListVo>>
     */
    @GetMapping("/discount")
    @Description("获取限时折扣分页列表")
    public Result<PageResult<ShopDiscountListVo>> list(@Validated PageParam pageParam,
                                                       @Validated ShopDiscountSearchParam searchParam) {
        PageResult<ShopDiscountListVo> list = shopDiscountService.getPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 限时折扣信息
     *
     * @param id 主键ID
     * @return Result<ShopActiveInfoVo>
     */
    @GetMapping("/discount/info/{id}")
    @Description("获取限时折扣信息")
    public Result<ShopDiscountInfoVo> info(@Validated @PathVariable("id") Integer id) {
        ShopDiscountInfoVo info = shopDiscountService.info(id);
        return Result.success(info);
    }

    /**
     * 限时折扣详情
     *
     * @param id 主键ID
     * @return Result<ShopActiveInfoVo>
     */
    @GetMapping("/discount/{id}")
    @Description("获取限时折扣详情")
    public Result<ShopDiscountDetailVo> detail(@Validated @PathVariable("id") Integer id) {
        ShopDiscountDetailVo detail = shopDiscountService.detail(id);
        return Result.success(detail);
    }


    /**
     * 限时折扣添加
     *
     * @param addParam
     * @return AjaxResult<Object>
     */
    @PostMapping("/discount")
    @Description("添加限时折扣")
    public Result<Object> add(@Validated @RequestBody ShopDiscountParam addParam) {
        shopDiscountService.add(addParam);
        return Result.success();
    }

    /**
     * 限时折扣编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/discount/{id}")
    @Description("编辑限时折扣")
    public Result<Object> edit(@Validated @PathVariable("id") Integer id, @Validated @RequestBody ShopDiscountParam editParam) {
        shopDiscountService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 限时折扣删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/discount/{id}")
    @Description("删除限时折扣")
    public Result<Object> del(@Validated @PathVariable("id") Integer id) {
        shopDiscountService.del(id);
        return Result.success();
    }

    /**
     * 获取限时折扣状态
     *
     * @return
     */
    @GetMapping("/status")
    @Description("获取限时折扣状态")
    public Result<Object> getStatus() {
        return Result.success(ActiveStatusEnum.getMap());
    }

    /**
     * 限时折扣关闭
     *
     * @param id
     * @return
     */
    @PutMapping("/discount/close/{id}")
    @Description("关闭限时折扣")
    public Result<Object> close(@Validated @PathVariable("id") Integer id) {
        shopDiscountService.discountClose(id);
        return Result.success();
    }

    /**
     * 活动订单列表
     *
     * @param activeId
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("discount/order/{id}")
    @Description("获取活动订单列表")
    public Result<PageResult<ShopDiscountOrderListVo>> order(@Validated @PathVariable("id") String activeId, @Validated PageParam pageParam, @Validated ShopDiscountOrderSearchParam searchParam) {
        return Result.success(shopDiscountService.order(Integer.valueOf(activeId), pageParam, searchParam));
    }

    /**
     * 活动会员列表
     *
     * @param discountId
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("/discount/member/{id}")
    @Description("获取活动会员列表")
    public Result<PageResult<ShopDiscountMemberListVo>> member(@Validated @PathVariable("id") String discountId, @Validated PageParam pageParam, @Validated ShopDiscountMemberSearchParam searchParam) {

        return Result.success(shopDiscountService.member(pageParam, Integer.valueOf(discountId), searchParam));
    }

    /**
     * 活动商品列表
     *
     * @param activeId
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("/discount/goods/{id}")
    @Description("获取活动商品列表")
    public Result<PageResult<ShopDiscountGoodsListVo>> goods(@Validated @PathVariable("id") String activeId, @Validated PageParam pageParam, @Validated ShopDiscountGoodsSearchParam searchParam) {

        return Result.success(shopDiscountService.goods(pageParam, Integer.valueOf(activeId), searchParam));
    }

    /**
     * 获取配置
     *
     * @return
     */
    @GetMapping("/discount/config")
    @Description("获取限时折扣配置")
    public Result<JSONArray> banner() {
        return Result.success(shopDiscountService.getDiscountBannerConfig());
    }

    /**
     * 活动配置
     *
     * @param config
     * @return
     */
    @PutMapping("/discount/config")
    @Description("保存限时折扣配置")
    public Result<Object> saveBanner(@Validated @RequestBody ShopDiscountConfigParam config) {

        shopDiscountService.setConfig("SHOP_DISCOUNT_BANNER_CONFIG", config.getList());
        return Result.success();
    }

    /**
     * 限时折扣商品校验
     */
    @PostMapping("/discount/goods/check")
    @Description("限时折扣商品校验")
    public Result<?> checkGoods(@Validated @RequestBody ShopDiscountCheckGoodsParam body) {
        return Result.success(shopDiscountService.checkGoodsData(body));
    }


    /**
     * 批量删除
     *
     * @return
     */
    @PostMapping("/discount/batchDelete")
    public Result batchDelete(@RequestBody ShopDiscountBatchParam param){
        shopDiscountService.batchDelete(param.getIds());
        return Result.success();
    }

    /**
     * 批量关闭
     *
     * @return
     */
    @PostMapping("/discount/batchClose")
    public Result batchClose(@RequestBody ShopDiscountBatchParam param){
        shopDiscountService.batchClose(param.getIds());
        return Result.success();
    }

}
