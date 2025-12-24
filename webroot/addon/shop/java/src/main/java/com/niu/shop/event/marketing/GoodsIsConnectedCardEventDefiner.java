package com.niu.shop.event.marketing;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;

import java.util.Map;

public abstract class GoodsIsConnectedCardEventDefiner extends CallbackListener<GoodsIsConnectedCardEventDefiner.GoodsIsConnectedCardEvent> {

    @Data
    public static class GoodsIsConnectedCardEvent extends Event {

        private Integer isAll;

        private Map<String, Object> where;

        private Integer[] goodsIds;
    }

    @Data
    public static class GoodsIsConnectedCardEventResult extends EventResult {
        private Boolean isConnected;
    }

    // 事件响应
    public abstract GoodsIsConnectedCardEventDefiner.GoodsIsConnectedCardEventResult handleCallback(GoodsIsConnectedCardEventDefiner.GoodsIsConnectedCardEvent event);
}
