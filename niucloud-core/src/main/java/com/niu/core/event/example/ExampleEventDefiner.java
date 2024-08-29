package com.niu.core.event.example;

import com.niu.core.component.context.event.Event;
import com.niu.core.component.context.event.EventResult;
import com.niu.core.component.context.listener.CallbackListener;
import lombok.Data;

/**
 * 作用: 示例
 * 描述: 带有返回值的事件定义.
 */
public abstract class ExampleEventDefiner extends CallbackListener<ExampleEventDefiner.ExampleEvent> {

    /**
     * 事件主题定义
     */
    @Data
    public static class ExampleEvent extends Event {

    }

    /**
     * 事件结果定义
     */
    @Data
    public static class ExampleEventResult extends EventResult {

        private String message;

    }

    // 事件响应
    public abstract ExampleEventResult handleCallback(ExampleEvent exampleEvent);


}
