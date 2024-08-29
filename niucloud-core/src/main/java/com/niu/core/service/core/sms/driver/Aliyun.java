package com.niu.core.service.core.sms.driver;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.core.common.loader.sms.BaseSms;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.dysmsapi20170525.Client;
import com.niu.core.common.loader.sms.SendResultVo;
import com.niu.core.enums.sys.SmsStatusEnum;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;

/**
 * 阿里云短信
 */
public class Aliyun extends BaseSms {

    private Client client;

    public Aliyun(JSONObject config) throws Exception {
        super(config);

        Config clientConfig = new com.aliyun.teaopenapi.models.Config();
        clientConfig.setAccessKeyId(config.getStr("app_key"));
        clientConfig.setAccessKeySecret(config.getStr("secret_key"));
        this.client = new Client(clientConfig);
    }

    /**
     * 发送短信
     */
    @Override
    public SendResultVo send(NoticeInfoVo notice, NoticeDataVo noticeData) {
        SendResultVo vo = new SendResultVo();
        try {
            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(noticeData.getMobile())
                    .setTemplateCode(notice.getSmsId())
                    .setTemplateParam(JSONUtil.parse(noticeData.getVars()).toString())
                    .setSignName(config.getStr("sign"));

            SendSmsResponse res = client.sendSmsWithOptions(request, new RuntimeOptions());

            if (res.getBody().getCode().equals("OK")) {
                vo.setStatus(SmsStatusEnum.SUCCESS);
            } else {
                vo.setStatus(SmsStatusEnum.FAIL);
                vo.setFailReason(res.getBody().getMessage());
            }
        } catch (TeaException e) {
            vo.setStatus(SmsStatusEnum.FAIL);
            vo.setFailReason(e.getMessage());
        } catch (Exception e) {
            vo.setStatus(SmsStatusEnum.FAIL);
            vo.setFailReason(e.getMessage());
        }
        return vo;
    }

}
