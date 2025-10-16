package com.niu.shop.event.refund;

import com.niu.core.common.component.context.event.Event;
import com.niu.shop.entity.order.ShopOrderRefund;
import lombok.Data;

import java.util.Map;

@Data
public class AfterShopOrderRefundApplyEvent extends Event {

    private String orderRefundNo;

    private ShopOrderRefund orderRefund;
}
