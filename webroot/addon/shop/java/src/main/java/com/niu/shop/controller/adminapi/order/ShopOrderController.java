package com.niu.shop.controller.adminapi.order;

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
import javax.annotation.Resource;

/**
 * 订单控制器
 */

@RestController
@RequestMapping("adminapi/shop/order")
public class ShopOrderController {

    @Resource
    IShopOrderService shopOrderService;

    @Resource
    IShopOrderFinishService shopOrderFinishService;

    /**
     * 订单列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopOrderListVo>>
     */
    @GetMapping("/list")
    public Result<PageResult<ShopOrderListVo>> list(@Validated PageParam pageParam, @Validated ShopOrderSearchParam searchParam) {
        PageResult<ShopOrderListVo> list = shopOrderService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 订单详情
     * @param id 主键ID
     * @return Result<ShopOrderInfoVo>
     */
    @GetMapping("/detail/{id}")
    public Result<ShopOrderInfoVo> info(@PathVariable Integer id) {
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
    public Result orderClose(@PathVariable Integer id) {
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
    public Result orderFinish(@PathVariable Integer id) {
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
    public Result status() {
        return Result.success(OrderStatusEnum.getMap());
    }

    /**
     * 订单来源
     *
     * @return
     */
    @GetMapping("/from")
    public Result getOrderFrom() {
        return Result.success(ChannelEnum.getMap());
    }

    /**
     * 订单支付方式
     *
     * @return
     */
    @GetMapping("/pay/type")
    public Result getPayType() {
        return Result.success(PayTypeEnum.getType());
    }

    /**
     * 订单调价
     *
     * @return
     */
    @PutMapping("/edit_price")
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
    public Result<EditDeliveryVo> getEditDeliveryData(EditDeliveryParam param) {
        return Result.success(shopOrderService.getEditDeliveryData(param));
    }

    /**
     * 修改订单配送
     *
     * @return
     */
    @PutMapping("/edit_delivery")
    public Result editDelivery(@Validated @RequestBody EditDeliveryParam param) {
        shopOrderService.editDelivery(param);
        return Result.success();
    }
}
