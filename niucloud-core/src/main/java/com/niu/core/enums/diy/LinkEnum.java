package com.niu.core.enums.diy;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class LinkEnum {
    public static JSONObject getLink(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "diy/links.json");
    }
}
