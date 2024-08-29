package com.niu.core.common.loader.sms;

import cn.hutool.json.JSONObject;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.SystemLoader;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class SmsLoader extends SystemLoader {

    /**
     * 获取所有消息驱动
     *
     * @return
     */
    public static JSONObject getType() {
        JSONObject jsonObject = JsonModuleLoader.build().mergeResultElement("sys/sms_type.json");
        return jsonObject;
    }

    /**
     * @param key
     * @return
     */
    public static BaseSms getDriver(String key, JSONObject config) {
        JSONObject jsonObject = SmsLoader.getType();
        JSONObject keyJson = jsonObject.getJSONObject(key);
        BaseSms base = BaseSms.forName(keyJson.getStr("driver", ""), config);
        if (base == null) throw new CommonException("短信驱动不存在");
        return base;
    }

}
