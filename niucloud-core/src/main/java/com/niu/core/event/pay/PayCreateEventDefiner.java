package com.niu.core.event.pay;

import com.niu.core.component.context.event.Event;
import com.niu.core.component.context.event.EventResult;
import com.niu.core.component.context.listener.CallbackListener;
import lombok.Data;

import java.math.BigDecimal;

public abstract class PayCreateEventDefiner extends CallbackListener<PayCreateEventDefiner.PayCreateEvent> {


    /**
     * 事件主题定义
     */
    @Data
    public static class PayCreateEvent extends Event {
        private String tradeType;

        private Integer tradeId;
    }

    /**
     * 事件结果定义
     */
    @Data
    public static class PayCreateEventResult extends EventResult {

        private String mainType;

        private Integer mainId;

        private BigDecimal money;

        private String tradeType;

        private Integer tradeId;

        private String body;
    }

    // 事件响应
    public abstract PayCreateEventDefiner.PayCreateEventResult handleCallback(PayCreateEventDefiner.PayCreateEvent event);

}
