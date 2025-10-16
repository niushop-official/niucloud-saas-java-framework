package com.niu.core.common.utils.json;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.*;

/**
 * JSON转换器
 */
public final class JsonConverterUtils {

    /**
     * 将JSON数组字符串解析添加到Set中
     *
     * @param jsonString ====> JSON数组
     * @param itemSet    ====> 列表
     * @param <T>
     * @return
     */
    public static <T> Set<T> jsonArrayAppendSet(String jsonString, Set<T> itemSet) {
        if (itemSet == null) {
            itemSet = new HashSet<>();
        }
        if (StrUtil.isEmpty(jsonString)) {
            return itemSet;
        }
        try {
            JSONArray jsonArray = JSONUtil.parseArray(jsonString);
            for (int i = 0; i < jsonArray.size(); i++) {
                itemSet.add((T) jsonArray.getObj(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemSet;
    }

    /**
     * 将JSON数组字符串解析添加到List中
     *
     * @param jsonString ====> JSON数组
     * @param itemList   ====> 列表
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonArrayAppendList(String jsonString, List<T> itemList) {
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        if (StrUtil.isEmpty(jsonString)) {
            return itemList;
        }
        try {
            JSONArray jsonArray = JSONUtil.parseArray(jsonString);
            for (int i = 0; i < jsonArray.size(); i++) {
                itemList.add((T) jsonArray.getObj(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemList;
    }

    /**
     * 将JSONObject中的键从驼峰格式转换为下划线格式（递归处理）
     * 支持处理嵌套的JSON字符串（如：将字符串类型的JSON解析为JSONObject或JSONArray后再递归处理）
     *
     * @param obj 待转换的JSONObject
     * @return 转换后的新JSONObject
     */
    public static JSONObject convertCamelToUnderline(Object obj) {
        JSONObject result = new JSONObject();
        if (obj == null || StrUtil.isEmpty(obj.toString())) {
            return result;
        }
        JSONObject json = new JSONObject();
        if (obj instanceof JSONObject) {
            json = (JSONObject) obj;
        } else if (obj instanceof Map) {
            json = new JSONObject((Map) obj);
        } else if (obj instanceof String) {
            json = JSONUtil.parseObj(obj.toString());
        }

        Iterator<String> keys = json.keySet().iterator();

        while (keys.hasNext()) {
            String originalKey = keys.next();
            Object value = json.get(originalKey);

            // 转换键名：驼峰转下划线
            String newKey = StrUtil.toUnderlineCase(originalKey);

            // 检查value是否为字符串类型的JSON
            if (value instanceof String) {
                String strValue = (String) value;
                if (JSONUtil.isTypeJSON(strValue)) {
                    try {
                        // 尝试解析为JSONObject或JSONArray
                        Object parsed = JSONUtil.parse(strValue);
                        value = parsed; // 替换value为解析后的JSON对象
                    } catch (Exception e) {
                        // 解析失败，保持原样
                    }
                }
            }

            // 递归处理嵌套的JSONObject和JSONArray
            if (value instanceof JSONObject) {
                JSONObject nestedJson = (JSONObject) value;
                result.put(newKey, convertCamelToUnderline(nestedJson));
            } else if (value instanceof JSONArray) {
                JSONArray originalArray = (JSONArray) value;
                JSONArray newArray = new JSONArray();

                for (Object item : originalArray) {
                    if (item instanceof JSONObject) {
                        newArray.add(convertCamelToUnderline((JSONObject) item));
                    } else if (item instanceof JSONArray) {
                        newArray.add(convertCamelToUnderlineForArray(item));
                    } else {
                        // 检查基本类型是否可能是JSON字符串
                        if (item instanceof String) {
                            String strItem = (String) item;
                            if (JSONUtil.isTypeJSON(strItem)) {
                                try {
                                    Object parsed = JSONUtil.parse(strItem);
                                    if (parsed instanceof JSONObject) {
                                        newArray.add(convertCamelToUnderline(parsed));
                                    } else if (parsed instanceof JSONArray) {
                                        newArray.add(convertCamelToUnderlineForArray(parsed));
                                    } else {
                                        newArray.add(item);
                                    }
                                } catch (Exception e) {
                                    newArray.add(item);
                                }
                            } else {
                                newArray.add(item);
                            }
                        } else {
                            newArray.add(item);
                        }
                    }
                }
                result.put(newKey, newArray);
            } else {
                result.put(newKey, value);
            }
        }
        return result;
    }

    /**
     * 将JSONArray中的键从驼峰格式转换为下划线格式（递归处理）
     * 支持处理嵌套的JSON字符串（如：将字符串类型的JSON解析为JSONObject或JSONArray后再递归处理）
     *
     * @param obj 待转换的对象，可以是JSONArray、List、数组或JSON字符串
     * @return 转换后的新JSONArray
     */
    public static JSONArray convertCamelToUnderlineForArray(Object obj) {
        JSONArray result = new JSONArray();
        if (obj == null || StrUtil.isEmpty(obj.toString())) {
            return result;
        }

        JSONArray jsonArray = new JSONArray();
        if (obj instanceof JSONArray) {
            jsonArray = (JSONArray) obj;
        } else if (obj instanceof List) {
            jsonArray = new JSONArray((List) obj);
        } else if (obj instanceof String) {
            jsonArray = JSONUtil.parseArray(obj.toString());
        }

        for (Object item : jsonArray) {
            if (item instanceof JSONObject) {
                // 处理JSONObject元素
                result.add(convertCamelToUnderline(item));
            } else if (item instanceof JSONArray) {
                // 处理嵌套的JSONArray
                result.add(convertCamelToUnderlineForArray(item));
            } else if (item instanceof Map) {
                // 处理Map元素
                JSONObject jsonObj = new JSONObject((Map) item);
                result.add(convertCamelToUnderline(jsonObj));
            } else if (item instanceof String) {
                // 检查字符串是否是JSON对象或数组
                String strValue = (String) item;
                if (JSONUtil.isTypeJSON(strValue)) {
                    try {
                        Object parsed = JSONUtil.parse(strValue);
                        if (parsed instanceof JSONObject) {
                            result.add(convertCamelToUnderline(parsed));
                        } else if (parsed instanceof JSONArray) {
                            result.add(convertCamelToUnderlineForArray(parsed));
                        } else {
                            result.add(item); // 普通字符串
                        }
                    } catch (Exception e) {
                        result.add(item); // 普通字符串
                    }
                } else {
                    result.add(item); // 普通字符串
                }
            } else {
                // 基本类型直接添加
                result.add(item);
            }
        }
        return result;
    }

}
