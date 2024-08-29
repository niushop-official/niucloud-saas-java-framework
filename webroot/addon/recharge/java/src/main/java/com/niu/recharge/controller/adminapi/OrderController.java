package com.niu.recharge.controller.adminapi;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.recharge.enums.OrderStatusEnum;
import com.niu.recharge.service.admin.IRechargeOrderService;
import com.niu.recharge.service.admin.param.RechargeOrderSearchParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("adminapi/recharge/order")
public class OrderController {

    @Resource
    IRechargeOrderService rechargeOrderService;

    @GetMapping("")
    public Result lists(PageParam pageParam, RechargeOrderSearchParam searchParam) {
        return Result.success(rechargeOrderService.list(pageParam, searchParam));
    }

    /**
     * 订单详情
     *
     * @return
     */
    @GetMapping("/{order_id}")
    public Result detail(@PathVariable("order_id") Integer orderId) {
        return Result.success(rechargeOrderService.detail(orderId));
    }

    /**
     * 订单状态
     *
     * @return
     */
    @GetMapping("/status")
    public Result<Map<Integer, Map<String, Object>>> status() {
        return Result.success(OrderStatusEnum.getMap());
    }

    /**
     * 订单统计
     *
     * @return
     */
    @GetMapping("/stat")
    public Result stat(RechargeOrderSearchParam searchParam) {
        return Result.success(rechargeOrderService.stat(searchParam));
    }
}
