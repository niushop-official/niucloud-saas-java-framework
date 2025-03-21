package com.niu.recharge.listener;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.refund.RefundSuccessEvent;
import com.niu.recharge.service.core.ICoreRechargeRefundService;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 支付成功
 */
@EventListen("recharge")
@Component
public class RefundSuccessListener extends AbstractListener {

    @Resource
    ICoreRechargeRefundService coreRechargeRefundService;

    public void handleEvent(RefundSuccessEvent event) {
        if (event.getTradeType().equals("recharge")) {
            coreRechargeRefundService.refundComplete(event.getRefund().getRefundNo());
        }
    }
}
