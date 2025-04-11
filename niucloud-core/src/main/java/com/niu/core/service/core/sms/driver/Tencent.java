package com.niu.core.service.core.sms.driver;

import cn.hutool.json.JSONObject;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.core.common.loader.sms.BaseSms;
import com.niu.core.common.loader.sms.SendResultVo;
import com.niu.core.enums.sys.SmsStatusEnum;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Tencent extends BaseSms {

    private SmsClient client;

    public Tencent(JSONObject config) throws Exception {
        super(config);
        Credential cred = new Credential(config.getStr("secret_id"), config.getStr("secret_key"));
        client = new SmsClient(cred, "ap-guangzhou");
    }

    /**
     * 发送短信
     */
    @Override
    public SendResultVo send(NoticeInfoVo notice, NoticeDataVo noticeData) {
        SendResultVo vo = new SendResultVo();
        try {
            SendSmsRequest req = new SendSmsRequest();
            req.setSmsSdkAppId(config.getStr("app_id"));
            req.setSignName(config.getStr("sign"));
            req.setTemplateId(notice.getSmsId());
            List<String> templateParam = new LinkedList<>();
            for (String key: noticeData.getVars().keySet()) {
                templateParam.add(noticeData.getVars().get(key).toString());
            }
            req.setTemplateParamSet(templateParam.toArray(String[]::new));
            req.setPhoneNumberSet(new String[]{noticeData.getMobile()});

            SendSmsResponse res = client.SendSms(req);

            if (res.getSendStatusSet()[0].getCode().equals("Ok")) {
                vo.setStatus(SmsStatusEnum.SUCCESS);
            } else {
                vo.setStatus(SmsStatusEnum.FAIL);
                vo.setFailReason(res.getSendStatusSet()[0].getMessage());
            }
        } catch (Exception e) {
            vo.setStatus(SmsStatusEnum.FAIL);
            vo.setFailReason(e.getMessage());
        }
        return vo;
    }
}
