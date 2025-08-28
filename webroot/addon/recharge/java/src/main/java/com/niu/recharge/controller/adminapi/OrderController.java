package com.niu.recharge.controller.adminapi;

import jdk.jfr.Description;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.recharge.enums.OrderStatusEnum;
import com.niu.recharge.service.admin.IRechargeOrderService;
import com.niu.recharge.service.admin.param.RechargeOrderSearchParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("adminapi/recharge/order")
@Description("充值订单管理")
public class OrderController {

    @Resource
    IRechargeOrderService rechargeOrderService;

    @GetMapping("")
    @Description("充值订单列表")
    public Result lists(PageParam pageParam, RechargeOrderSearchParam searchParam) {
        return Result.success(rechargeOrderService.list(pageParam, searchParam));
    }

    /**
     * 订单详情
     *
     * @return
     */
    @GetMapping("/{order_id}")
    @Description("订单详情")
    public Result detail(@PathVariable("order_id") Integer orderId) {
        return Result.success(rechargeOrderService.detail(orderId));
    }

    /**
     * 订单状态
     *
     * @return
     */
    @GetMapping("/status")
    @Description("订单状态")
    public Result<Map<Integer, Map<String, Object>>> status() {
        return Result.success(OrderStatusEnum.getMap());
    }

    /**
     * 订单统计
     *
     * @return
     */
    @GetMapping("/stat")
    @Description("订单统计")
    public Result stat(RechargeOrderSearchParam searchParam) {
        return Result.success(rechargeOrderService.stat(searchParam));
    }
}
