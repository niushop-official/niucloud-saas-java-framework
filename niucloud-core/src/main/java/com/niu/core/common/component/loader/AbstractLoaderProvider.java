package com.niu.core.common.component.loader;

import cn.hutool.json.JSONObject;
import com.niu.core.common.component.context.SpringContext;
import org.springframework.context.ApplicationContext;

import java.util.Collection;

public abstract class AbstractLoaderProvider {

    /**
     * 获取ApplicationContext
     *
     * @return
     */
    protected ApplicationContext getApplicationContext() {
        return SpringContext.get();
    }


    /**
     * @param jsonObjectList
     * @return
     */
    protected JSONObject mergeJsonForAttribute(Collection<JSONObject> jsonObjectList) {
        JSONObject targetMap = new JSONObject();
        for (JSONObject jsonObject : jsonObjectList) {
            jsonObject.keySet().stream().forEach(key -> {
                Object value = targetMap.get(key);
                if (value == null) {
                    targetMap.put(key, jsonObject.get(key));
                } else {
                    JSONObject jsonValue1 = (JSONObject) value;
                    JSONObject jsonValue2 = (JSONObject) jsonObject.get(key);
                    jsonValue2.keySet().stream().forEach(k -> {
                        jsonValue1.put(k, jsonValue2.get(k));
                    });
                    targetMap.put(key, jsonValue1);
                }
            });
        }
        return targetMap;
    }

}
