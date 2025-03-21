package com.niu.core.enums.diy;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class ComponentEnum {

    public static JSONObject getComponent(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "diy/components.json");
    }

    public static JSONObject getComponent(String name){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "diy/components.json");
    }
}
