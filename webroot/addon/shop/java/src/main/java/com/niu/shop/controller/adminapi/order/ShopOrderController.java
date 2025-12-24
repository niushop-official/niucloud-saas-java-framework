package com.niu.shop.controller.adminapi.order;

import com.niu.shop.enums.order.OrderFromTypeEnum;
import jdk.jfr.Description;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.service.admin.order.IShopOrderFinishService;
import com.niu.shop.service.admin.order.param.*;
import com.niu.shop.service.admin.order.IShopOrderService;
import com.niu.shop.service.admin.order.vo.EditDeliveryVo;
import com.niu.shop.service.admin.order.vo.ShopOrderInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

/**
 * 订单控制器
 */

@RestController
@RequestMapping("adminapi/shop/order")
@Description("订单管理")
public class ShopOrderController {

    @Resource
    IShopOrderService shopOrderService;

    @Resource
    IShopOrderFinishService shopOrderFinishService;

    /**
     * 订单列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < ShopOrderListVo>>
     */
    @GetMapping("/list")
    @Description("获取订单分页列表")
    public Result<PageResult<ShopOrderListVo>> list(@Validated PageParam pageParam, @Validated ShopOrderSearchParam searchParam) {
        PageResult<ShopOrderListVo> list = shopOrderService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 订单详情
     *
     * @param id 主键ID
     * @return Result<ShopOrderInfoVo>
     */
    @GetMapping("/detail/{id}")
    @Description("获取订单详情")
    public Result<ShopOrderInfoVo> info(@Validated @PathVariable("id") Integer id) {
        ShopOrderInfoVo info = shopOrderService.detail(id);
        return Result.success(info);
    }

    /**
     * 关闭订单
     *
     * @param id
     * @return
     */
    @PutMapping("/close/{id}")
    @Description("关闭订单")
    public Result orderClose(@Validated @PathVariable("id") Integer id) {
        shopOrderService.close(id);
        return Result.success();
    }

    /**
     * 订单完成
     *
     * @param id
     * @return
     */
    @PutMapping("/finish/{id}")
    @Description("完成订单")
    public Result orderFinish(@Validated @PathVariable("id") Integer id) {
        shopOrderFinishService.finish(id);
        return Result.success();
    }

    /**
     * 商家备注
     *
     * @param param
     * @return
     */
    @PutMapping("/shop_remark")
    @Description("设置商家备注")
    public Result setShopRemark(@Validated @RequestBody SetShopRemarkParam param) {
        shopOrderService.setShopRemark(param);
        return Result.success();
    }

    /**
     * 订单状态
     *
     * @return
     */
    @GetMapping("/status")
    @Description("获取订单状态")
    public Result status() {
        return Result.success(OrderStatusEnum.getMap());
    }

    /**
     * 订单来源
     *
     * @return
     */
    @GetMapping("/from")
    @Description("获取订单来源")
    public Result getOrderFrom() {
        return Result.success(OrderFromTypeEnum.getOrderFrom());
    }

    /**
     * 订单支付方式
     *
     * @return
     */
    @GetMapping("/pay/type")
    @Description("获取订单支付方式")
    public Result getPayType() {
        return Result.success(PayTypeEnum.getType());
    }

    /**
     * 订单调价
     *
     * @return
     */
    @PutMapping("/edit_price")
    @Description("订单调价")
    public Result editPrice(@Validated @RequestBody EditPriceParam param) {
        shopOrderService.editPrice(param);
        return Result.success();
    }

    /**
     * 获取订单配送修改信息
     *
     * @return
     */
    @GetMapping("/edit_delivery")
    @Description("获取订单配送修改信息")
    public Result<EditDeliveryVo> getEditDeliveryData(@Validated EditDeliveryParam param) {
        return Result.success(shopOrderService.getEditDeliveryData(param));
    }

    /**
     * 修改订单配送
     *
     * @return
     */
    @PutMapping("/edit_delivery")
    @Description("修改订单配送")
    public Result editDelivery(@Validated @RequestBody EditDeliveryParam param) {
        shopOrderService.editDelivery(param);
        return Result.success();
    }

    @PostMapping("/delete")
    @Description("删除订单")
    public Result delete(@Validated @RequestBody DeleteOrderParam param) {
        shopOrderService.delete(param);
        return Result.success();
    }

}
