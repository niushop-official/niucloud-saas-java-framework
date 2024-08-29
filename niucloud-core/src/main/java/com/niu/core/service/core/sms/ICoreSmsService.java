package com.niu.core.service.core.sms;

import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;

public interface ICoreSmsService {

    void send(Integer siteId, NoticeInfoVo notice, NoticeDataVo noticeData);
}
