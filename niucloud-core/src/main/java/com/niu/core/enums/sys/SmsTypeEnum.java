package com.niu.core.enums.sys;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class SmsTypeEnum {

    /**
     * 短信类型
     *
     * @return
     */
    public static JSONObject getType(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "sys/sms_type.json");
    }
}
