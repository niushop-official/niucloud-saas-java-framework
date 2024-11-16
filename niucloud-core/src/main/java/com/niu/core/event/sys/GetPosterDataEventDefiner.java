package com.niu.core.event.sys;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;

/**
 * 获取海报数据
 */
public abstract class GetPosterDataEventDefiner extends CallbackListener<GetPosterDataEventDefiner.GetPosterDataEvent> {

    @Data
    public static class GetPosterDataEvent extends Event {
        private String type;
    }

    @Data
    public static class GetPosterDataResult extends EventResult {
    }

    public abstract GetPosterDataEventDefiner.GetPosterDataResult handleCallback(GetPosterDataEventDefiner.GetPosterDataEvent event);
}
