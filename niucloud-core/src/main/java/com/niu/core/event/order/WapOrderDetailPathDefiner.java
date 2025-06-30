package com.niu.core.event.order;

import cn.hutool.json.JSONObject;
import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.core.entity.pay.Pay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

public abstract class WapOrderDetailPathDefiner extends CallbackListener<WapOrderDetailPathDefiner.WapOrderDetailPathEvent> {

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WapOrderDetailPathEvent extends Event {
        private Pay pay;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class WapOrderDetailPathResult extends EventResult {
        private String path;
    }

}

