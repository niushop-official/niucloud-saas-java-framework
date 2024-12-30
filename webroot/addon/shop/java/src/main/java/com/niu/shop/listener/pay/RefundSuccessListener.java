package com.niu.shop.listener.pay;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.refund.RefundSuccessEvent;
import com.niu.shop.service.core.goods.ICoreGoodsStatService;
import com.niu.shop.service.core.refund.ICoreRefundService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 退款成功
 */
@EventListen("shop")
@Component
public class RefundSuccessListener extends AbstractListener {

    @Resource
    ICoreRefundService coreRefundService;

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    public void handleEvent(RefundSuccessEvent event) {
        if (event.getTradeType().equals("shop")) {
            coreRefundService.refundTransferSuccess(event.getRefund());
            coreGoodsStatService.saveGoodsRefundNumAndMoneyByOrderId(event.getRefund());
        }
    }
}
