package com.niu.core.component.sms.impl;

import cn.hutool.json.JSONObject;
import com.niu.core.component.sms.AbstractSmsProvider;
import com.niu.core.component.sms.ISmsProvider;
import com.niu.core.component.sms.annotation.SmsProvider;
import com.niu.core.component.sms.model.SmsSend;
import com.niu.core.component.sms.model.SmsSendResult;

@SmsProvider(name = "tencent", source = "core")
public class TencentSmsProviderImpl extends AbstractSmsProvider implements ISmsProvider {

    @Override
    public void setName() {
        this.name = "腾讯云短信";
    }

    @Override
    public void setParams() {
        this.params.put("app_id", "APP_ID");
        this.params.put("app_key", "APP_KEY");
        this.params.put("sign", "短信签名");
        this.params.put("secret_key", "APP_SECRET");
    }

    @Override
    public void setComponent() {
        this.component = "/src/app/views/setting/components/sms-tencent.vue";
    }

    @Override
    public SmsSendResult send(JSONObject configObject, SmsSend smsSend) {
        System.out.println("configObject = " + configObject);
        System.out.println("smsSend = " + smsSend);
        System.out.println("--------------------------------------这里是腾讯云发送短信--------------------------------------");
        return new SmsSendResult();
    }

}
