package com.niu.shop.controller.api.refund;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.shop.enums.order.OrderRefundTypeEnum;
import com.niu.shop.service.api.goods.refund.IRefundActionService;
import com.niu.shop.service.api.goods.refund.param.ApplyRefundParam;
import com.niu.shop.service.api.goods.refund.param.EditRefundParam;
import com.niu.shop.service.api.goods.refund.param.RefundDeliveryParam;
import com.niu.shop.service.api.goods.refund.param.RefundSearchParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("api/shop")
@SaCheckLogin
public class RefundController {

    @Resource
    IRefundActionService refundActionService;

    /**
     * 订单维权列表
     *
     * @return
     */
    @GetMapping("/order/refund")
    public Result page(@Validated PageParam pageParam, @Validated RefundSearchParam searchParam) {
        return Result.success(refundActionService.page(pageParam, searchParam));
    }

    /**
     * 订单维权详情
     *
     * @return
     */
    @GetMapping("/order/refund/{order_refund_no}")
    public Result detail(@PathVariable(name = "order_refund_no") String orderRefundNo) {
        return Result.success(refundActionService.detail(orderRefundNo));
    }

    /**
     * 查询订单项退款信息
     *
     * @return
     */
    @GetMapping("/refund/refund_data")
    public Result getRefundData(@RequestParam(name = "order_goods_id") Integer orderGoodsId) {
        return Result.success(refundActionService.getRefundData(orderGoodsId));
    }

    /**
     * 查询订单项退款信息
     *
     * @return
     */
    @GetMapping("/refund/refund_data_by_no")
    public Result getRefundDataByOrderRefundNo(@RequestParam(name = "order_refund_no") String orderRefundNo) {
        return Result.success(refundActionService.getRefundData(orderRefundNo));
    }

    /**
     * 查询订单项退款信息
     *
     * @return
     */
    @PostMapping("/refund/apply")
    public Result apply(@Validated @RequestBody ApplyRefundParam param) {
        refundActionService.apply(param);
        return Result.success();
    }

    /**
     * 修改退款申请
     *
     * @return
     */
    @PutMapping("/refund/{order_refund_no}")
    public Result edit(@PathVariable(name = "order_refund_no") String orderRefundNo, @Validated @RequestBody EditRefundParam param) {
        param.setOrderRefundNo(orderRefundNo);
        refundActionService.edit(param);
        return Result.success();
    }

    /**
     * 维权退货
     *
     * @return
     */
    @PostMapping("/refund/delivery/{order_refund_no}")
    public Result delivery(@PathVariable(name = "order_refund_no") String orderRefundNo, @Validated @RequestBody RefundDeliveryParam param) {
        param.setOrderRefundNo(orderRefundNo);
        refundActionService.delivery(param);
        return Result.success();
    }

    /**
     * 修改维权退货信息
     *
     * @return
     */
    @PutMapping("/refund/delivery/{order_refund_no}")
    public Result editDelivery(@PathVariable(name = "order_refund_no") String orderRefundNo, @Validated @RequestBody RefundDeliveryParam param) {
        param.setOrderRefundNo(orderRefundNo);
        refundActionService.editDelivery(param);
        return Result.success();
    }

    /**
     * 修改维权退货信息
     *
     * @return
     */
    @PutMapping("/refund/close/{order_refund_no}")
    public Result close(@PathVariable(name = "order_refund_no") String orderRefundNo) {
        refundActionService.close(orderRefundNo);
        return Result.success();
    }

    /**
     * 退款原因
     *
     * @return
     */
    @GetMapping("/refund/reason")
    public Result getRefundReason() {
        String[] reason = {
                "未按约定时间发货",
                "拍错/多拍/不喜欢",
                "协商一致退款",
                "其他"
        };
        return Result.success(reason);
    }

    /**
     * 退款方式
     *
     * @return
     */
    @GetMapping("/order/refund/type")
    public Result getRefundType() {
        return Result.success(OrderRefundTypeEnum.getMap());
    }
}
