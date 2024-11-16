package com.niu.core.event.sys;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.Data;

import java.util.Map;

public abstract class GetQrcodeOfChannelDefiner extends CallbackListener<GetQrcodeOfChannelDefiner.GetQrcodeOfChannelEvent> {

    @Data
    public static class GetQrcodeOfChannelEvent extends Event {
        private String url;

        private String page;

        private Map<String, Object> data;

        private String channel;

        private Boolean outfile;

        private String filePath;
    }

    @Data
    public static class GetQrcodeOfChannelResult extends EventResult {
        private String path;
    }

    public abstract GetQrcodeOfChannelResult handleCallback(GetQrcodeOfChannelEvent event);
}
