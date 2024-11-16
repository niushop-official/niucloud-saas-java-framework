package com.niu.core.event.notice;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.DefaultEventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;

/**
 * 事件定义者
 */
public abstract class SendNoticeEventDefiner extends CallbackListener<SendNoticeEventDefiner.SendNoticeEvent> {

    /**
     * 事件主题
     */
    public static class SendNoticeEvent extends Event {

        private String key;

        private NoticeInfoVo notice;

        private NoticeDataVo noticeData;

        public SendNoticeEvent setKey(String key) {
            this.key = key;
            return this;
        }

        public SendNoticeEvent setNotice(NoticeInfoVo notice) {
            this.notice = notice;
            return this;
        }

        public SendNoticeEvent setNoticeData(NoticeDataVo noticeData) {
            this.noticeData = noticeData;
            return this;
        }

        public String getKey() {
            return this.key;
        }

        public NoticeInfoVo getNotice() {
            return this.notice;
        }

        public NoticeDataVo getNoticeData() {
            return this.noticeData;
        }

    }

    /**
     * 事件响应（事件返回使用默认已定义的类）
     *
     * @param event
     * @return
     */
    public abstract DefaultEventResult handleCallback(SendNoticeEvent event);

}
