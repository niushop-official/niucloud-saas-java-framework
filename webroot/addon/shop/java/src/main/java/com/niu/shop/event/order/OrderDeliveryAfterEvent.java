package com.niu.shop.event.order;

import com.niu.core.common.component.context.event.Event;
import com.niu.shop.entity.order.ShopOrder;
import lombok.Data;

@Data
public class OrderDeliveryAfterEvent  extends Event {
    private ShopOrder order;
}
