package com.niu.core.enums.member;


import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;

/**
 * 成长值规则
 */
public class GrowthRuleEnum {

    public static JSONObject getType(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement("member/growth_rule.json");
    }
}
