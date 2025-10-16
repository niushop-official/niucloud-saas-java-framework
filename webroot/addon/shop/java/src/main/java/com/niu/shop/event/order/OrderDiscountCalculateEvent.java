package com.niu.shop.event.order;

import com.niu.core.common.component.context.event.Event;
import com.niu.shop.service.core.order.param.OrderCalculateParam;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import lombok.Data;

@Data
public class OrderDiscountCalculateEvent extends Event {

    private OrderCreateDataVo vo;

    private OrderCalculateParam param;
}
