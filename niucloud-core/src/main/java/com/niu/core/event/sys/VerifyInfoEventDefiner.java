package com.niu.core.event.sys;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.core.service.admin.verify.vo.VerifyInfoVo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 事件定义者： 核销操作
 */
public abstract class VerifyInfoEventDefiner extends CallbackListener<VerifyInfoEventDefiner.VerifyInfoEvent> {

    /**
     * 事件主题： 核销创建
     */
    @Data
    @Accessors(chain = true)
    public static class VerifyInfoEvent extends Event {
        private VerifyInfoVo data;
    }

    @Data
    @Accessors(chain = true)
    public static class VerifyInfoEventResult extends EventResult {
        private Map<String, Object> result;
    }

    // 事件响应
    public abstract VerifyInfoEventResult handleCallback(VerifyInfoEvent verifyEvent);

}
