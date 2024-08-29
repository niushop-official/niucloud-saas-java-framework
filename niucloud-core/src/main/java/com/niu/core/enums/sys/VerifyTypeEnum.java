package com.niu.core.enums.sys;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class VerifyTypeEnum {

    public static JSONObject getType(){
        return JsonModuleLoader.build().mergeResultElement("verify_type.json");
    }

    public static String getTypeByName(String type){
        JSONObject allType = getType();
        if (allType.getJSONObject(type) != null) {
            return allType.getJSONObject(type).getStr("name");
        } else {
            return "";
        }
    }
}
