package com.niu.recharge.controller.api;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.recharge.service.api.IRechargeOrderService;
import com.niu.recharge.service.api.param.RechargeOrderSearchParam;
import com.niu.recharge.service.api.vo.RechargeOrderInfoVo;
import com.niu.recharge.service.api.vo.RechargeOrderListVo;
import com.niu.recharge.service.core.param.RechargeOrderCreateParam;
import com.niu.recharge.service.core.vo.RechargeOrderCreateVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/recharge")
public class RechargeController {

    @Resource
    IRechargeOrderService rechargeOrderService;

    /**
     * 充值订单创建
     *
     * @param param
     * @return
     */
    @PostMapping("/recharge")
    public Result<RechargeOrderCreateVo> create(@Validated @RequestBody RechargeOrderCreateParam param) {
        return Result.success(rechargeOrderService.create(param));
    }

    /**
     * 订单列表
     *
     * @param
     * @return
     */
    @GetMapping("/recharge")
    public Result<PageResult<RechargeOrderListVo>> list(PageParam pageParam, RechargeOrderSearchParam searchParam) {
        return Result.success(rechargeOrderService.list(pageParam, searchParam));
    }

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    @GetMapping("/recharge/{order_id}")
    public Result<RechargeOrderInfoVo> detail(@PathVariable("order_id") Integer orderId) {
        return Result.success(rechargeOrderService.detail(orderId));
    }
}
