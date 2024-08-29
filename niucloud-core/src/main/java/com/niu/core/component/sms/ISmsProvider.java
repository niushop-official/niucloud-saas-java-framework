package com.niu.core.component.sms;

import cn.hutool.json.JSONObject;
import com.niu.core.component.sms.model.SmsSend;
import com.niu.core.component.sms.model.SmsSendResult;

public interface ISmsProvider {

    /**
     * @param configObject
     * @param smsSend
     * @return
     */
    public SmsSendResult send(JSONObject configObject, SmsSend smsSend);

}
