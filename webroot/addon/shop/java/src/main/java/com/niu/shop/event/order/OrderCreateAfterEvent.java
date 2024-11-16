package com.niu.shop.event.order;

import com.niu.core.common.component.context.event.Event;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import lombok.Data;

@Data
public class OrderCreateAfterEvent extends Event {

    private OrderCreateDataVo orderData;

    private ShopOrder order;
}
