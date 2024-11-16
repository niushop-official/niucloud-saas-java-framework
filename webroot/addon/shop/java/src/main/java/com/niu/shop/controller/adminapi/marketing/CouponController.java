package com.niu.shop.controller.adminapi.marketing;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.admin.marketing.ICouponService;
import com.niu.shop.service.admin.marketing.param.ShopActiveParam;
import com.niu.shop.service.admin.marketing.param.ShopCouponParam;
import com.niu.shop.service.admin.marketing.param.ShopCouponSearchParam;
import com.niu.shop.service.admin.marketing.vo.ShopCouponInfoVo;
import com.niu.shop.service.admin.marketing.vo.ShopCouponListVo;
import com.niu.shop.service.admin.marketing.vo.ShopCouponMemberListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 优惠券控制器
 */

@RestController
@RequestMapping("adminapi/shop/goods/coupon")
public class CouponController {

    @Resource
    ICouponService couponService;


    @GetMapping("/init")
    public Result<JSONObject> init() {
        return Result.success(couponService.getInit());
    }
    /**
     * 优惠券列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopCouponListVo>>
     */
    @GetMapping("")
    public Result<PageResult<ShopCouponListVo>> list(@Validated PageParam pageParam,
                                                     @Validated ShopCouponSearchParam searchParam) {
        PageResult<ShopCouponListVo> list = couponService.getPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 优惠券详情
     * @param id 主键ID
     * @return Result<ShopCouponInfoVo>
     */
    @GetMapping("/detail/{id}")
    public Result<ShopCouponInfoVo> info(@Validated @PathVariable("id") Integer id) {
        ShopCouponInfoVo info = couponService.getInfo(id);
        return Result.success(info);
    }

    /**
     * 优惠券添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody ShopCouponParam addParam) {
        couponService.add(addParam);
        return Result.success();
    }

    /**
     * 优惠券编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/edit/{id}")
    public Result<Object> edit(@Validated @PathVariable("id") Integer id, @Validated @RequestBody ShopCouponParam editParam) {
        couponService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 优惠券删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@Validated @PathVariable Integer id) {
        couponService.delete(id);
        return Result.success();
    }

    /**
     * 获取会员优惠券列表
     * @param pageParam
     * @param id
     * @param keywords
     * @return
     */
    @GetMapping("/records")
    public Result<PageResult<ShopCouponMemberListVo>> getMemberCoupon(@Validated PageParam pageParam, Integer id, String keywords) {
        return Result.success(couponService.getMemberCoupon(pageParam, id, keywords));
    }
    /**
     * 获取选中优惠券
     * @param couponIds
     * @return
     */
    @GetMapping("/selected")
    public List<ShopCouponListVo> getSelectedLists(@Validated @RequestBody String couponIds){
        return couponService.getSelectedList(couponIds);
    }

    /**
     * 设置优惠券领用状态
     * @param status
     * @param id
     * @return
     */
    @PutMapping("setstatus/{status}")
    public Result<Object> setCouponStatus(@Validated @PathVariable("status") Integer status, @Validated @RequestBody Integer id){
        couponService.setStatus(id, status);
        return Result.success();
    }

    /**
     * 优惠券失效
     * @param id
     * @return
     */
    @PutMapping("invalid/{id}")
    public Result<Object> couponInvalid(@Validated @PathVariable("id") Integer id){
        couponService.couponInvalid(id);
        return Result.success();
    }
}
