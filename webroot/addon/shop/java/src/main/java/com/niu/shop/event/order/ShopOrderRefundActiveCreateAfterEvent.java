package com.niu.shop.event.order;

import com.niu.core.common.component.context.event.Event;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.entity.order.ShopOrderRefund;
import com.niu.shop.service.core.refund.param.ActiveRefundParam;
import lombok.Data;

@Data
public class ShopOrderRefundActiveCreateAfterEvent extends Event {

    private ActiveRefundParam refundParam;

    private ShopOrderRefund refund;

    private ShopOrderGoods orderGoods;
}
