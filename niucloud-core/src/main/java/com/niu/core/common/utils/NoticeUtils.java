package com.niu.core.common.utils;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.service.core.notice.ICoreNoticeService;

import java.util.Map;

public class NoticeUtils {

    /**
     * 发送消息
     *
     * @param siteId
     * @param key
     * @param data
     */
    public static void send(Integer siteId, String key, Map<String, Object> data) {
        ICoreNoticeService coreNoticeService = SpringContext.bean(ICoreNoticeService.class);
        coreNoticeService.send(siteId, key, data);
    }

}
