package com.niu.core.event.diy;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;

/**
 * 自定义表单删除前事件
 */
public abstract class DiyFormDelBeforeEventDefiner extends CallbackListener<DiyFormDelBeforeEventDefiner.DiyFormDelBeforeEvent> {

    /**
     * 事件主题定义
     */
    @Data
    public static class DiyFormDelBeforeEvent extends Event {
        private Integer formId;
    }

    /**
     * 事件结果定义
     */
    @Data
    public static class DiyFromDelBeforeResult extends EventResult {

        private Boolean allowOperate;

    }

    // 事件响应
    public abstract DiyFromDelBeforeResult handleCallback(DiyFormDelBeforeEvent diyFormDelBeforeEvent);


}
