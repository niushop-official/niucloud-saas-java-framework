package com.niu.core.enums.member;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;

/**
 * 获取账户变动方式
 */
public class MemberAccountChangeTypeEnum {

    public static JSONObject getType(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement("account_change_type.json");
    }

    public static JSONObject getType(String accountType){
        JSONObject jsonObject = MemberAccountChangeTypeEnum.getType();
        return jsonObject.getJSONObject(accountType);
    }
}
