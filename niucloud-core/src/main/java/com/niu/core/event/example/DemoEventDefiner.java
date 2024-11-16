package com.niu.core.event.example;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;

/**
 * 作用: 示例
 * 描述: 带有返回值的事件定义.
 */
public abstract class DemoEventDefiner extends CallbackListener<DemoEventDefiner.DemoEvent> {

    /**
     * 事件主题定义
     */
    @Data
    public static class DemoEvent extends Event {

    }

    /**
     * 事件结果定义
     */
    @Data
    public static class DemoEventResult extends EventResult {

        private String message;

    }

    // 事件响应
    public abstract DemoEventResult handleCallback(DemoEvent businessEvent);

}
