package com.niu.core.common.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.niu.core.entity.sys.SysMenu;

import java.util.ArrayList;
import java.util.List;

public class JacksonUtils {

    /**
     * 将对象转换成小写下划线
     *
     * @param object
     * @return
     */
    public static String toSnakeCaseJSONString(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    /**
     * 将JSON字符串<下划线>转换为对象<驼峰>
     *
     * @param jsonString
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T toSnakeCaseObject(String jsonString, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        // 配置下划线到驼峰命名的转换
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        // 禁用自动关闭流特性
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        T result = null;
        try {
            result = mapper.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将JSON字符串<下划线>转换为对象<驼峰>
     *
     * @param jsonObjectList
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> toSnakeCaseList(List<cn.hutool.json.JSONObject> jsonObjectList, Class<T> clazz) {
        if (jsonObjectList == null || jsonObjectList.size() <= 0) {
            return new ArrayList<>();
        }
        List<T> beanList = new ArrayList<>();
        jsonObjectList.stream().forEach(json -> {
            T target = JacksonUtils.toSnakeCaseObject(json.toString(), clazz);
            beanList.add(target);
        });
        return beanList;
    }

}
