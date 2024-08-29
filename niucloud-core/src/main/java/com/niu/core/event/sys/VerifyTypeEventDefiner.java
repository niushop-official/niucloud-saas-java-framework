package com.niu.core.event.sys;

import com.niu.core.component.context.event.Event;
import com.niu.core.component.context.event.EventResult;
import com.niu.core.component.context.listener.CallbackListener;
import lombok.Data;

/**
 * 事件定义者
 */
public abstract class VerifyTypeEventDefiner extends CallbackListener<VerifyTypeEventDefiner.VerifyTypeEvent> {

    /**
     * 事件主题：核销类型
     */
    @Data
    public static class VerifyTypeEvent extends Event {

        private String source;

    }

    @Data
    public static class VerifyTypeEventResult extends EventResult {

    }

    // 事件响应
    public abstract VerifyTypeEventResult handleCallback(VerifyTypeEvent verifyTypeEvent);

}
