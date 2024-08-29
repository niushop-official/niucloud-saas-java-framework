package com.niu.core.common.utils.json;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

}
