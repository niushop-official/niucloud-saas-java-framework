package com.niu.core.event.sys;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.core.common.domain.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 通用事件返回类
 */
public abstract class CommonEventDefiner extends CallbackListener<CommonEventDefiner.CommonDataEvent> {

    // 事件响应
    public abstract CommonDataEventResult handleCallback(CommonDataEvent exportDataEvent);

    /**
     * 事件主题： 数据导出类型
     * <p>
     * 定义传入参数
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(chain = true)
    public static class CommonDataEvent extends Event {

        /**
         * 站点id
         */
        private Integer siteId;

        private JSONObject data;
    }

    /**
     * 事件结果
     * <p>
     * 返回数据类型
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(chain = true)
    public static class CommonDataEventResult extends EventResult {

        /**
         * 返回的结果集
         */
        private JSONObject resultData;
    }

}
