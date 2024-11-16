package com.niu.shop.event.order;

import com.niu.core.common.component.context.event.Event;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderRefund;
import lombok.Data;

@Data
public class OrderRefundFinishAfterEvent extends Event {

    private ShopOrderRefund refund;

}
