package com.niu.shop.listener.order;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.entity.pay.Pay;
import com.niu.core.event.order.WapOrderDetailPathDefiner;
import org.springframework.stereotype.Component;

@EventCallback("shop")
@Component
public class WapOrderDetailPathListener extends WapOrderDetailPathDefiner {

    @Override
    public WapOrderDetailPathResult handleCallback(WapOrderDetailPathEvent event) {
        Pay pay = event.getPay();
        if (pay.getTradeType().equals("shop")) {
            WapOrderDetailPathResult result = new WapOrderDetailPathResult();
            result.setPath("addon/shop/pages/order/detail?order_id=" + pay.getTradeId());
            return result;
        }
        return null;
    }
}
