package com.niu.core.enums.member;


import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;

/**
 * 积分规则
 */
public class PointRuleEnum {

    public static JSONObject getType(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement("member/point_rule.json");
    }
}
