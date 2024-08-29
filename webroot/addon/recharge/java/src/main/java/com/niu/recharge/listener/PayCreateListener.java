package com.niu.recharge.listener;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.exception.CommonException;
import com.niu.core.event.pay.PayCreateEventDefiner;
import com.niu.recharge.entity.RechargeOrder;
import com.niu.recharge.enums.OrderStatusEnum;
import com.niu.recharge.service.core.ICoreRechargeOrderService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 一对多的事件监听，且有返回值
 */
@EventCallback("recharge")
@Component
public class PayCreateListener extends PayCreateEventDefiner {

    @Resource
    ICoreRechargeOrderService coreRechargeOrderService;

    @Override
    public PayCreateEventDefiner.PayCreateEventResult handleCallback(PayCreateEventDefiner.PayCreateEvent event) {
        if (!event.getTradeType().equals("recharge")) return null;

        RechargeOrder order = coreRechargeOrderService.orderInfo(event.getTradeId());
        if (!order.getOrderStatus().equals(OrderStatusEnum.WAIT_PAY.getStatus())) throw new CommonException("只有待支付的订单可以支付");

        PayCreateEventDefiner.PayCreateEventResult result = new PayCreateEventDefiner.PayCreateEventResult();
        result.setTradeId(event.getTradeId());
        result.setTradeType(event.getTradeType());
        result.setMainType("member");
        result.setMainId(order.getMemberId());
        result.setMoney(order.getOrderMoney());
        result.setBody("充值订单");

        return result;
    }

}
