package com.niu.core.enums.diy_form;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class DiyFormComponentEnum {

    /**
     * 获取全部模版
     * @return
     */
    public JSONObject getComponent(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement("diy_form/component.json");
    }

    /**
     * 模版名称
     * @param componentName
     * @return
     */
    public JSONObject getComponent(String componentName){
        JSONObject all = getComponent();

        for (String key : all.keySet()) {
            JSONObject temp = all.getJSONObject(key);
            JSONObject list = temp.getJSONObject("list");
            for (String key1 : list.keySet()) {
                JSONObject temp1 = list.getJSONObject(key1);
                if(key1.equals(componentName)){
                    return temp1;
                }
            }
        }
        return null;
    }

}
