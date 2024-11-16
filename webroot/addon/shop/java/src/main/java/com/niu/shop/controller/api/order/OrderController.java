package com.niu.shop.controller.api.order;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.service.api.order.IOrderService;
import com.niu.shop.service.api.order.param.DeliveryPackageSearchParam;
import com.niu.shop.service.api.order.param.ShopOrderSearchParam;
import com.niu.shop.service.core.order.ICoreOrderConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/shop/order")
@SaCheckLogin
public class OrderController {

    @Resource
    IOrderService orderService;

    @Resource
    ICoreOrderConfigService coreOrderConfigService;

    /**
     * 查询订单列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("")
    public Result page(@Validated PageParam pageParam, @Validated ShopOrderSearchParam searchParam) {
        return Result.success(orderService.page(pageParam, searchParam));
    }

    /**
     * 获取订单详情
     *
     * @param orderId
     * @return
     */
    @GetMapping("/{order_id}")
    public Result detail(@PathVariable(name = "order_id") Integer orderId) {
        return Result.success(orderService.detail(orderId));
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
     * 订单数量
     *
     * @return
     */
    @GetMapping("/num")
    public Result getNum() {
        return Result.success(orderService.getNum());
    }

    /**
     * 获取交易设置
     *
     * @return
     */
    @GetMapping("/config")
    public Result orderConfig() {
        return Result.success(coreOrderConfigService.getConfig(RequestUtils.siteId()));
    }

    /**
     * 关闭订单
     *
     * @param id
     * @return
     */
    @PutMapping("/close/{id}")
    public Result orderClose(@PathVariable Integer id) {
        orderService.close(id);
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
        orderService.orderFinish(id);
        return Result.success();
    }

    /**
     * 物流跟踪
     *
     * @return
     */
    @GetMapping("/logistics")
    public Result getPackage(DeliveryPackageSearchParam searchParam) {
        return Result.success(orderService.getPackage(searchParam));
    }
}
