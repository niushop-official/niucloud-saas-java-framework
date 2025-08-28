package com.niu.shop.controller.adminapi.marketing;

import com.niu.shop.enums.coupon.SendCouponRangeTypeEnum;
import com.niu.shop.service.admin.marketing.param.*;
import jdk.jfr.Description;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.shop.enums.coupon.CouponStatusEnum;
import com.niu.shop.service.admin.marketing.ICouponService;
import com.niu.shop.service.admin.marketing.vo.ShopCouponInfoVo;
import com.niu.shop.service.admin.marketing.vo.ShopCouponListVo;
import com.niu.shop.service.admin.marketing.vo.ShopCouponMemberListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.HashMap;
import java.util.Map;

/**
 * 优惠券控制器
 */

@RestController
@RequestMapping("adminapi/shop/goods/coupon")
@Description("优惠券管理")
public class CouponController {

    @Resource
    ICouponService couponService;


    @GetMapping("/init")
    @Description("获取优惠券初始化数据")
    public Result<JSONObject> init() {
        return Result.success(couponService.getInit());
    }

    /**
     * 优惠券列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < ShopCouponListVo>>
     */
    @GetMapping("")
    @Description("获取优惠券分页列表")
    public Result<PageResult<ShopCouponListVo>> list(@Validated PageParam pageParam,
                                                     @Validated ShopCouponSearchParam searchParam) {
        PageResult<ShopCouponListVo> list = couponService.getPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 优惠券详情
     *
     * @param id 主键ID
     * @return Result<ShopCouponInfoVo>
     */
    @GetMapping("/detail/{id}")
    @Description("获取优惠券详情")
    public Result<ShopCouponInfoVo> info(@Validated @PathVariable("id") Integer id) {
        ShopCouponInfoVo info = couponService.getInfo(id);
        return Result.success(info);
    }

    /**
     * 优惠券添加
     *
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    @Description("添加优惠券")
    public Result<Object> add(@Validated @RequestBody ShopCouponParam addParam) {
        couponService.add(addParam);
        return Result.success();
    }

    /**
     * 优惠券编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/edit/{id}")
    @Description("编辑优惠券")
    public Result<Object> edit(@Validated @PathVariable("id") Integer id, @Validated @RequestBody ShopCouponParam editParam) {
        couponService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 优惠券删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    @Description("删除优惠券")
    public Result<Object> del(@Validated @PathVariable("id") Integer id) {
        couponService.delete(id);
        return Result.success();
    }

    @PostMapping("/delete")
    @Description("批量删除优惠券")
    public Result<Object> del(@RequestBody ShopCouponDelParam shopCouponDelParam) {
        couponService.deleteAll(shopCouponDelParam);
        return Result.success();
    }

    /**
     * 获取会员优惠券列表
     *
     * @param pageParam
     * @param id
     * @param keywords
     * @return
     */
    @GetMapping("/records")
    @Description("获取会员优惠券列表")
    public Result<PageResult<ShopCouponMemberListVo>> getMemberCoupon(@Validated PageParam pageParam, @RequestParam("id") Integer id, @RequestParam("keywords") String keywords) {
        return Result.success(couponService.getMemberCoupon(pageParam, id, keywords));
    }

    /**
     * 选中优惠券
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("/select")
    @Description("获取可选优惠券分页列表")
    public Result<?> getSelectPage(PageParam pageParam, ShopCouponSelectParam searchParam) {
        return Result.success(couponService.getSelectPage(pageParam, searchParam));
    }

    /**
     * 获取选中优惠券
     *
     * @param couponId
     * @return
     */
    @GetMapping("/selected")
    @Description("获取已选优惠券列表")
    public Result<?> getSelectedLists(@Validated @RequestParam("coupon_id") String couponId) {
        return Result.success(couponService.getSelectedList(couponId));
    }

    @GetMapping("/status")
    @Description("获取优惠券状态")
    public Result<Object> getCouponStatus() {
        return Result.success(CouponStatusEnum.getMap());
    }

    /**
     * 设置优惠券领用状态
     *
     * @param status
     * @param id
     * @return
     */
    @PutMapping("setstatus/{status}")
    @Description("设置优惠券领用状态")
    public Result<Object> setCouponStatus(@Validated @PathVariable("status") Integer status, @Validated @RequestBody Integer id) {
        couponService.setStatus(id, status);
        return Result.success();
    }

    /**
     * 优惠券失效
     *
     * @param id
     * @return
     */
    @PutMapping("invalid/{id}")
    @Description("优惠券失效")
    public Result<Object> couponInvalid(@Validated @PathVariable("id") Integer id) {
        couponService.couponInvalid(id);
        return Result.success();
    }

    /**
     * 批量优惠券失效
     *
     * @param shopCouponDelParam
     * @return
     */
    @PutMapping("invalid")
    @Description("优惠券失效")
    public Result<Object> couponInvalidAll(@RequestBody ShopCouponDelParam shopCouponDelParam) {
        couponService.couponInvalidAll(shopCouponDelParam);
        return Result.success();
    }

    /**
     * 获取发送优惠券范围初始化数据
     */
    @GetMapping("/send/init")
    @Description("获取发送优惠券范围初始化数据")
    public Result<?> getSendInit() {
        Map<String, Map<String, String>> result = new HashMap<>();
        result.put("range_type_list", SendCouponRangeTypeEnum.getMap());
        return Result.success(result);
    }

    /**
     * 获取发送记录分页列表
     */
    @GetMapping("/send/pages/{couponId}")
    @Description("获取发送记录分页列表")
    public Result<?> getSendPages(@Validated PageParam pageParam, @Validated @PathVariable("couponId") Integer couponId, ShopCouponSendPageParam param) {
        return Result.success(couponService.getSendPages(pageParam, couponId, param));
    }

    /**
     * 发放优惠券
     */
    @PostMapping("/send/{couponId}")
    @Description("发放优惠券")
    public Result<?> send(@Validated @PathVariable("couponId") Integer couponId, @Validated @RequestBody ShopCouponSendParam param){
        couponService.sendCoupon(couponId, param);
        return Result.success();
    }
}
