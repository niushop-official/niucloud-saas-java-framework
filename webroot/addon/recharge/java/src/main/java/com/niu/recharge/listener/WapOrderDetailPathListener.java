package com.niu.recharge.listener;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.entity.pay.Pay;
import com.niu.core.event.order.WapOrderDetailPathDefiner;
import org.springframework.stereotype.Component;

@EventCallback("recharge")
@Component
public class WapOrderDetailPathListener extends WapOrderDetailPathDefiner {

    @Override
    public WapOrderDetailPathDefiner.WapOrderDetailPathResult handleCallback(WapOrderDetailPathDefiner.WapOrderDetailPathEvent event) {
        Pay pay = event.getPay();
        if (pay.getTradeType().equals("recharge")) {
            WapOrderDetailPathDefiner.WapOrderDetailPathResult result = new WapOrderDetailPathDefiner.WapOrderDetailPathResult();
            result.setPath("addon/recharge/pages/recharge_record_detail?id=" + pay.getTradeId());
            return result;
        }
        return null;
    }
}
