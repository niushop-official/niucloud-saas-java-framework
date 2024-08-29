package com.niu.core.enums.member;


import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;

/**
 * 会员等级样式
 */
public class MemberLevelStyleEnum {

    public static JSONObject getStyle(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement("member/level_style.json");
    }
}
