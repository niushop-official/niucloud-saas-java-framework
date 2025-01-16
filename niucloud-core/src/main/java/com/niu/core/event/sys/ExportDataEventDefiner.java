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
 * 数据导出事件定义者
 */
public abstract class ExportDataEventDefiner extends CallbackListener<ExportDataEventDefiner.ExportDataEvent> {

    // 事件响应
    public abstract ExportDataEventResult handleCallback(ExportDataEvent exportDataEvent);

    /**
     * 事件主题： 数据导出类型
     * <p>
     * 定义传入参数
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(chain = true)
    public static class ExportDataEvent extends Event {

        /**
         * 站点id
         */
        private Integer siteId;

        /**
         * 数据导出类型
         */
        private String type;

        /**
         * 查询条件
         */
        private JSONObject where;

        /**
         * 分页参数
         */
        private PageParam pageParam;
    }

    /**
     * 事件结果
     * <p>
     * 返回数据类型
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(chain = true)
    public static class ExportDataEventResult extends EventResult {

        /**
         * 返回的结果集
         */
        private JSONArray resultData;
    }

}
