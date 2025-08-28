package com.niu.shop.enums.delivery;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThirdTypeEnum {
    private static final String DATA = "dada";

    public static Map<String, ThirdTypeValue> getThirdType(String type) {
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONArray schedule = jsonModuleLoader.getResultSet("shop", "third_type.json");
        Map<String, ThirdTypeValue> result = new HashMap<>();
        for (int i = 0; i < schedule.size(); i++) {
            JSONObject jsonObject = schedule.getJSONObject(i);
            String key = jsonObject.keySet().iterator().next().toString();
            ThirdTypeValue thirdTypeValue = BeanUtil.copyProperties(jsonObject.get(key), ThirdTypeValue.class);
            result.put(key, thirdTypeValue);
        }
        if (StringUtils.isNotEmpty(type)) {
            for (Map.Entry<String, ThirdTypeValue> entry : result.entrySet()) {
                if (entry.getKey().equals(type)) {
                    Map<String, ThirdTypeValue> resultMap = new HashMap<>();
                    resultMap.put(entry.getKey(), entry.getValue());
                    return resultMap;
                }
            }
        }
        return result;
    }

    @Data
    public static class ThirdTypeValue {
        private String name;
        private List<String> column;
        private List<String> encryptParams;
    }
}
