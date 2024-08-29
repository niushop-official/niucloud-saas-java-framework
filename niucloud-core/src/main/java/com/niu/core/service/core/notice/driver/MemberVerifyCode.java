package com.niu.core.service.core.notice.driver;

import com.niu.core.common.loader.notice.BaseNotice;
import com.niu.core.common.loader.notice.NoticeDataVo;

import java.util.HashMap;
import java.util.Map;

public class MemberVerifyCode extends BaseNotice {

    public NoticeDataVo noticeData(Map<String, Object> vars) {
        NoticeDataVo vo = new NoticeDataVo();
        vo.setMobile((String) vars.get("mobile"));
        Map<String, Object> data = new HashMap<>();
        data.put("code", vars.get("code"));
        vo.setVars(data);
        return vo;
    }
}
