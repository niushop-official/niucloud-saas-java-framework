package com.niu.recharge.listener;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.component.context.listener.AbstractListener;
import com.niu.core.event.pay.PaySuccessEvent;
import com.niu.recharge.service.core.ICoreRechargeOrderService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 支付成功
 */
@EventListen("recharge")
@Component
public class PaySuccessListener extends AbstractListener {

    @Resource
    ICoreRechargeOrderService coreRechargeOrderService;

    public void handleEvent(PaySuccessEvent event) {
        if (event.getTradeType().equals("recharge")) {
            coreRechargeOrderService.pay(event.getPay());
        }
    }
}
