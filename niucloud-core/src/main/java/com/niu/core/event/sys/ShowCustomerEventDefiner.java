package com.niu.core.event.sys;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

public abstract class ShowCustomerEventDefiner extends CallbackListener<ShowCustomerEventDefiner.ShowCustomerEvent> {

    @Data
    @Accessors(chain = true)
    public static class ShowCustomerEvent extends Event {


    }

    /**
     * 事件结果
     */
    @Data
    @Accessors(chain = true)
    public static class ShowCustomerEventResult extends EventResult {

        private Map<String, List<MenuItem>> data;

    }

    @Data
    public static class MenuItem{
        private String title;
        private String desc;
        private String icon;
        private String key;
        private String url;

        public MenuItem(String title, String desc, String icon, String key, String url) {
            this.title = title;
            this.desc = desc;
            this.icon = icon;
            this.key = key;
            this.url = url;
        }
    }

    // 事件响应
    public abstract ShowCustomerEventDefiner.ShowCustomerEventResult handleCallback(ShowCustomerEventDefiner.ShowCustomerEvent event);
}
