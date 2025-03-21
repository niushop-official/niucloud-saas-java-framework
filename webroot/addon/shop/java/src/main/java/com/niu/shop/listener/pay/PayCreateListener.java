package com.niu.shop.listener.pay;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.exception.CommonException;
import com.niu.core.event.pay.PayCreateEventDefiner;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.service.core.order.ICoreOrderService;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 获取支付数据
 */
@EventCallback("shop")
@Component
public class PayCreateListener extends PayCreateEventDefiner {

    @Resource
    ICoreOrderService coreOrderService;

    @Override
    public PayCreateEventResult handleCallback(PayCreateEvent event) {
        if (!event.getTradeType().equals("shop")) return null;

        ShopOrder order = coreOrderService.orderInfo(event.getTradeId());
        if (order == null) throw new CommonException("订单不存在");
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_PAY.getStatus().toString())) throw new CommonException("只有待支付的订单可以支付");

        PayCreateEventResult result = new PayCreateEventResult();
        result.setTradeId(event.getTradeId());
        result.setTradeType(event.getTradeType());
        result.setMainType("member");
        result.setMainId(order.getMemberId());
        result.setMoney(order.getOrderMoney());
        result.setBody(order.getBody());

        return result;
    }

}
