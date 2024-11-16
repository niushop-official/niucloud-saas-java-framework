package com.niu.shop.event.order;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import lombok.Data;

public abstract class ShopOrderDiscountCreateEventDefiner extends CallbackListener<ShopOrderDiscountCreateEventDefiner.ShopOrderDiscountCreateEvent> {

    /**
     * 事件主题定义
     */
    @Data
    public static class ShopOrderDiscountCreateEvent extends Event {

        private OrderCreateDataVo.Discount discount;

        private OrderCreateDataVo orderCreateData;
    }

    /**
     * 事件结果定义
     */
    @Data
    public static class ShopOrderDiscountCreateEventResult extends EventResult {

    }

    // 事件响应
    public abstract ShopOrderDiscountCreateEventDefiner.ShopOrderDiscountCreateEventResult handleCallback(ShopOrderDiscountCreateEventDefiner.ShopOrderDiscountCreateEvent event);
}
