package com.niu.shop.controller.adminapi.marketing;

import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.service.admin.marketing.IShopDiscountService;
import com.niu.shop.service.admin.marketing.param.*;
import com.niu.shop.service.admin.marketing.IShopActiveService;
import com.niu.shop.service.admin.marketing.vo.*;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 限时折扣控制器
 */

@RestController
@RequestMapping("adminapi/shop/active")
public class ShopDiscountController {

    @Resource
    IShopDiscountService shopDiscountService;

    /**
     * 限时折扣列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopActiveListVo>>
     */
    @GetMapping("/discount")
    public Result<PageResult<ShopDiscountListVo>> list(@Validated PageParam pageParam,
                                                       @Validated ShopDiscountSearchParam searchParam) {
        PageResult<ShopDiscountListVo> list = shopDiscountService.getPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 限时折扣详情
     * @param id 主键ID
     * @return Result<ShopActiveInfoVo>
     */
    @GetMapping("/discount/{id}")
    public Result<ShopDiscountDetailVo> detail(@Validated @PathVariable("id") Integer id) {
        ShopDiscountDetailVo detail = shopDiscountService.detail(id);
        return Result.success(detail);
    }



    /**
     * 限时折扣添加
     * @param addParam
     * @return AjaxResult<Object>
     */
    @PostMapping("/discount")
    public Result<Object> add(@Validated @RequestBody ShopDiscountParam addParam) {
        shopDiscountService.add(addParam);
        return Result.success();
    }

    /**
     * 限时折扣编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/discount/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody ShopDiscountParam editParam) {
        shopDiscountService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 限时折扣删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/discount/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        shopDiscountService.del(id);
        return Result.success();
    }
    /**
     * 获取限时折扣状态
     * @return
     */
    @GetMapping("/status")
    public Result<Object> getStatus(){
        return Result.success(ActiveStatusEnum.getMap());
    }

    /**
     * 限时折扣关闭
     * @param id
     * @return
     */
    @PutMapping("/discount/close/{id}")
    public Result<Object> close(@PathVariable("id") Integer id) {
        shopDiscountService.discountClose(id);
        return Result.success();
    }

    /**
     * 活动订单列表
     * @param activeId
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("discount/order/{id}")
    public Result<PageResult<ShopDiscountOrderListVo>> order(@PathVariable("id") String activeId, PageParam pageParam, ShopDiscountOrderSearchParam searchParam) {
        return Result.success(shopDiscountService.order(Integer.valueOf(activeId), pageParam, searchParam));
    }

    /**
     * 活动会员列表
     * @param activeId
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("/discount/member/{id}")
    public Result<PageResult<ShopDiscountMemberListVo>> member(@PathVariable("id") String activeId, PageParam pageParam, ShopDiscountMemberSearchParam searchParam) {

        return Result.success(shopDiscountService.member(pageParam, Integer.valueOf(activeId), searchParam));
    }

    /**
     * 活动商品列表
     * @param activeId
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("/discount/goods/{id}")
    public Result<PageResult<ShopDiscountGoodsListVo>> goods(@PathVariable("id") String activeId, PageParam pageParam, ShopDiscountGoodsSearchParam searchParam) {

        return Result.success(shopDiscountService.goods(pageParam, Integer.valueOf(activeId), searchParam));
    }

    /**
     * 获取配置
     * @return
     */
    @GetMapping("/discount/config")
    public Result<JSONArray> banner(){
        return Result.success(shopDiscountService.getDiscountBannerConfig());
    }

    /**
     * 活动配置
     * @param config
     * @return
     */
    @PutMapping("/discount/config")
    public Result <Object> saveBanner(@RequestBody  ShopDiscountConfigParam config) {

        shopDiscountService.setConfig("SHOP_DISCOUNT_BANNER_CONFIG", config.getList());
        return Result.success();
    }





}