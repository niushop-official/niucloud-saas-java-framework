package com.niu.core.event.sys;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 事件定义者
 */
public abstract class VerifyCheckEventDefiner extends CallbackListener<VerifyCheckEventDefiner.VerifyCheckEvent> {

    /**
     * 事件主题： 核销创建
     */
    @Data
    @Accessors(chain = true)
    public static class VerifyCheckEvent extends Event {

        private Map<String, Object> data;

    }

    /**
     * 事件结果
     */
    @Data
    @Accessors(chain = true)
    public static class VerifyCheckEventResult extends EventResult {

        private Map<String, Object> data;

    }

    // 事件响应
    public abstract VerifyCheckEventResult handleCallback(VerifyCheckEvent verifyCheckEvent);

}
