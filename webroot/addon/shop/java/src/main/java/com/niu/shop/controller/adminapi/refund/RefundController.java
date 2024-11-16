package com.niu.shop.controller.adminapi.refund;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.admin.refund.IRefundActionService;
import com.niu.shop.service.admin.refund.param.AuditApplyParam;
import com.niu.shop.service.admin.refund.param.AuditRefundGoodsParam;
import com.niu.shop.service.admin.refund.param.RefundSearchParam;
import com.niu.shop.service.admin.shop_address.IShopAddressService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("adminapi/shop/order/refund")
public class RefundController {

    @Resource
    IRefundActionService refundActionService;

    @Resource
    IShopAddressService shopAddressService;

    /**
     * 订单维权列表
     *
     * @return
     */
    @GetMapping("")
    public Result page(@Validated PageParam pageParam, @Validated RefundSearchParam searchParam) {
        return Result.success(refundActionService.page(pageParam, searchParam));
    }

    /**
     * 订单维权详情
     *
     * @return
     */
    @GetMapping("/{id}")
    public Result page(@PathVariable Integer id) {
        return Result.success(refundActionService.detail(id));
    }

    /**
     * 订单维权审核
     *
     * @param orderRefundNo
     * @return
     */
    @PutMapping("/audit/{order_refund_no}")
    public Result auditApply(@PathVariable(name = "order_refund_no") String orderRefundNo, @Validated @RequestBody AuditApplyParam param) {
        param.setOrderRefundNo(orderRefundNo);
        refundActionService.auditApply(param);
        return Result.success();
    }

    /**
     * 订单退货审核
     *
     * @param orderRefundNo
     * @return
     */
    @PutMapping("/delivery/{order_refund_no}")
    public Result auditRefundGoods(@PathVariable(name = "order_refund_no") String orderRefundNo, @Validated @RequestBody AuditRefundGoodsParam param) {
        param.setOrderRefundNo(orderRefundNo);
        refundActionService.auditRefundGoods(param);
        return Result.success();
    }

    /**
     * 退款地址列表
     *
     * @return
     */
    @GetMapping("/address")
    public Result refundAddress() {
        return Result.success(shopAddressService.refundAddress());
    }
}
