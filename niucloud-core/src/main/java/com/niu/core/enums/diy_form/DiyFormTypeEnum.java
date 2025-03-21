package com.niu.core.enums.diy_form;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class DiyFormTypeEnum {

    /**
     * 获取全部模版
     * @return
     */
    public JSONObject getType(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement("diy_form/type.json");
    }

    /**
     * 模版名称
     * @param typeName
     * @return
     */
    public JSONObject getType(String typeName){
        JSONObject all = getType();

        for (String key : all.keySet()) {
            JSONObject temp = all.getJSONObject(key);
            if (key.equals(typeName)) {
                return temp;
            }
        }
        return null;
    }
}
