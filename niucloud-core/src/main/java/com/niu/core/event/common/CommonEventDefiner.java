package com.niu.core.event.common;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;

/**
 * 作用: 普通定义
 * 描述: 带有返回值的事件定义.
 */
public abstract class CommonEventDefiner extends CallbackListener<CommonEventDefiner.CommonEvent> {

    /**
     * 事件主题定义
     */
    @Data
    public static class CommonEvent extends Event {
        
        /**
         * 事件名称
         */
        private String eventName;

        /**
         * 定义数据对象
         */
        private Object data;


        /**
         * 关联会员id
         */
        private Integer memberId = 0;
    }

    /**
     * 事件结果定义
     */
    @Data
    public static class CommonEventResult extends EventResult {

        private Object result;

    }

    // 事件响应
    public abstract CommonEventResult handleCallback(CommonEvent commonEvent);


}
