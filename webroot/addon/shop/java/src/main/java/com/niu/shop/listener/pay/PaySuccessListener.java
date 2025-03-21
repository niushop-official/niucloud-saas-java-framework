package com.niu.shop.listener.pay;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.pay.PaySuccessEvent;
import com.niu.shop.service.core.goods.ICoreGoodsStatService;
import com.niu.shop.service.core.order.ICoreOrderService;
import com.niu.shop.service.core.order.param.OrderPayParam;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 支付成功
 */
@EventListen("shop")
@Component
public class PaySuccessListener extends AbstractListener {

    @Resource
    ICoreOrderService coreOrderService;

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    public void handleEvent(PaySuccessEvent event) {
        if (event.getTradeType().equals("shop")) {
            OrderPayParam param = new OrderPayParam();
            param.setTradeId(event.getTradeId());
            param.setOutTradeNo(event.getPay().getOutTradeNo());
            coreOrderService.pay(param);

            coreGoodsStatService.saveGoodsPayNumAndMoneyByOrderId(event.getPay().getTradeId());
        }
    }
}
