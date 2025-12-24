package com.niu.core.enums.diy;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class PagesEnum {

    public static JSONObject getPages(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "diy/pages.json");
    }

    public static JSONObject getPagesByAddon(String type, String addon){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject pages = jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "diy/pages.json");

        if (type != null && !type.isEmpty() && pages != null) {
            pages = pages.getJSONObject(type);
        }

        return pages;
    }

    public static JSONObject getPages(String type, String mode){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject pages = jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "diy/pages.json");

        if (ObjectUtil.isNotEmpty(type) && ObjectUtil.isNotEmpty(pages)) {
            pages = pages.getJSONObject(type);
        }
        if (ObjectUtil.isNotEmpty(mode) && ObjectUtil.isNotEmpty(pages)) {
            JSONObject modePages = new JSONObject();
            for (String key : pages.keySet()) {
                if (!pages.getStr(key + ".mode", "").equals(mode)) {
                    modePages.put(key, pages.getJSONObject(key));
                }
            }
            return modePages;
        }
        return pages;
    }
}
