package com.niu.core.event.sys;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.DefaultEventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 事件定义者： 核销操作
 */
public abstract class VerifyEventDefiner extends CallbackListener<VerifyEventDefiner.VerifyEvent> {

    /**
     * 事件主题： 核销创建
     */
    @Data
    @Accessors(chain = true)
    public static class VerifyEvent extends Event {

        private String code;

        private Map<String, Object> data;

        private String value;

        private String type;

        private String body;

        private String relateTag;

        private Long createTime;

        private Integer verifierMemberId;

        /**
         * @param dataMap
         */
        public void build(Map<String, Object> dataMap) {
            if (dataMap != null) {
                this.code = (String) dataMap.get("code");
                this.data = (Map<String, Object>) dataMap.get("data");
                this.value = (String) dataMap.get("value");
                this.type = (String) dataMap.get("type");
                this.body = (String) dataMap.get("body");
                this.relateTag = (String) dataMap.get("relate_tag");
                this.createTime = (Long) dataMap.get("create_time");
                this.verifierMemberId = (Integer) dataMap.get("verifier_member_id");
            }
        }
    }

    // 事件响应
    public abstract DefaultEventResult handleCallback(VerifyEvent verifyEvent);

}
