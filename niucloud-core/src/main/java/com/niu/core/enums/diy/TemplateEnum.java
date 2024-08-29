package com.niu.core.enums.diy;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.service.admin.diy.param.TemplateParam;

public class TemplateEnum {

    public static JSONObject getTemplate(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement("diy/template.json");
    }

    public static JSONObject getTemplate(TemplateParam param){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject diyTemplate = jsonModuleLoader.mergeResultElement("diy/template.json");

        JSONObject template = new JSONObject();

        if (!param.getType().isEmpty()) {
            for (String key: diyTemplate.keySet()) {
                if (ObjectUtil.defaultIfNull(diyTemplate.getByPath(key + ".type", String.class), "").equals(param.getType())) {
                    template.put(key, diyTemplate.getJSONObject(key));
                }
            }
            return template;
        }
        if (!param.getAction().isEmpty()) {
            for (String key: diyTemplate.keySet()) {
                if (ObjectUtil.defaultIfNull(diyTemplate.getByPath(key + ".action", String.class), "").equals(param.getAction())) {
                    template.put(key, diyTemplate.getJSONObject(key));
                }
            }
            return template;
        }
        if (param.getKey() != null && param.getKey().length > 0) {
            for (String key: param.getKey()) {
                if (diyTemplate.getJSONObject(key) != null) {
                    template.put(key, diyTemplate.getJSONObject(key));
                }
            }
            return template;
        }

        return diyTemplate;
    }
}
