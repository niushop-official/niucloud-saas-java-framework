package com.niu.core.event.sys;

import cn.hutool.json.JSONObject;
import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

public abstract class ExportDataTypeEventDefiner extends CallbackListener<ExportDataTypeEventDefiner.ExportDataTypeEvent> {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ExportDataTypeEvent extends Event {
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ExportDataTypeResult extends EventResult {
        private JSONObject result;
    }

}
