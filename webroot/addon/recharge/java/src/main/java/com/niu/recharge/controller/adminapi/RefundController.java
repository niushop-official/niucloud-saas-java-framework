package com.niu.recharge.controller.adminapi;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.recharge.enums.RefundStatusEnum;
import com.niu.recharge.service.admin.IRechargeRefundService;
import com.niu.recharge.service.admin.param.RechargeRefundSearchParam;
import com.niu.recharge.service.admin.vo.RechargeRefundStatVo;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("adminapi/recharge/refund")
public class RefundController {

    @Resource
    IRechargeRefundService rechargeRefundService;

    /**
     * 退款列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("")
    public Result lists(PageParam pageParam, RechargeRefundSearchParam searchParam) {
        return Result.success(rechargeRefundService.list(pageParam, searchParam));
    }

    /**
     * 发起退款
     *
     * @param orderId
     * @return
     */
    @PutMapping("/{order_id}")
    public Result refund(@PathVariable("order_id") Integer orderId) {
        rechargeRefundService.refund(orderId);
        return Result.success();
    }

    /**
     * 退款详情
     *
     * @return
     */
    @GetMapping("/{refund_id}")
    public Result detail(@PathVariable("refund_id") Integer refundId) {
        return Result.success(rechargeRefundService.detail(refundId));
    }

    /**
     * 退款状态
     *
     * @return
     */
    @GetMapping("/status")
    public Result<Map<Integer, Map<String, Object>>> status() {
        return Result.success(RefundStatusEnum.getMap());
    }

    /**
     * 退款统计
     *
     * @return
     */
    @GetMapping("/stat")
    public Result<RechargeRefundStatVo> stat(RechargeRefundSearchParam searchParam) {
        return Result.success(rechargeRefundService.stat(searchParam));
    }
}
