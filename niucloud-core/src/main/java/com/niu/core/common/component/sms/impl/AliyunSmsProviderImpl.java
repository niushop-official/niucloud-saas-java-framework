package com.niu.core.common.component.sms.impl;

import cn.hutool.json.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.niu.core.common.component.sms.model.SmsSendResult;
import com.niu.core.common.component.sms.AbstractSmsProvider;
import com.niu.core.common.component.sms.ISmsProvider;
import com.niu.core.common.component.sms.annotation.SmsProvider;
import com.niu.core.common.component.sms.model.SmsSend;
import com.niu.core.enums.sys.SmsStatusEnum;

@SmsProvider(name = "aliyun", source = "core")
public class AliyunSmsProviderImpl extends AbstractSmsProvider implements ISmsProvider {

    @Override
    public void setName() {
        this.name = "阿里云短信";
    }

    @Override
    public void setParams() {
        this.params.put("app_key", "APP_KEY");
        this.params.put("secret_key", "APP_SECRET");
        this.params.put("sign", "短信签名");
    }

    @Override
    public void setComponent() {
        this.component = "/src/app/views/setting/components/sms-ali.vue";
    }

    @Override
    public SmsSendResult send(JSONObject configObject, SmsSend smsSend) {
        SmsSendResult smsSendResult = new SmsSendResult();
        Client client = null;
        try {
            Config clientConfig = new com.aliyun.teaopenapi.models.Config();
            clientConfig.setAccessKeyId((String) this.params.get("app_key"));
            clientConfig.setAccessKeySecret((String) this.params.get("secret_key"));
            client = new Client(clientConfig);
            // 请求
            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(smsSend.getMobile())
                    .setTemplateCode(smsSend.getTemplateId())
                    .setTemplateParam(smsSend.getMessage())
                    .setSignName((String) this.params.get("sign"));
            // 响应
            SendSmsResponse res = client.sendSmsWithOptions(request, new RuntimeOptions());
            if (res.getBody().getCode().equals("OK")) {
                smsSendResult.setStatus(SmsStatusEnum.SUCCESS);
            } else {
                smsSendResult.setStatus(SmsStatusEnum.FAIL);
                smsSendResult.setFailReason(res.getBody().getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return smsSendResult;
    }

}
