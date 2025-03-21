package com.niu.core.enums.diy_form;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class DiyFormTemplateEnum {

    /**
     * 获取全部模版
     * @return
     */
    public JSONObject getTemplate(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement("diy_form/template.json");
    }

    /**
     * 模版名称
     * @return
     */
    public JSONObject getTemplate(String type, String templateKey){
        JSONObject all = getTemplate();

        for (String key : all.keySet()) {
            JSONObject temp = all.getJSONObject(key);
            if (key.equals(type)) {
                if(ObjectUtil.isNotEmpty(templateKey) && temp.containsKey(templateKey))
                {
                    return temp.getJSONObject(templateKey);
                }
            }
        }
        return all;
    }


}
