package com.niu.core.common.utils.json;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import com.alibaba.fastjson2.JSON;

public class JsonViewUtils {

    /**
     * 将数据中的JSON字符串展示到页面并且为对象
     *
     * @param value
     * @return
     */
    public static Object toJsonObject(String value) {
        if (!StrUtil.isEmpty(value)) {
            if (value.startsWith("[")) {
                return JSON.parseArray(value);
            } else if (value.startsWith("{")) {
                return JSON.parseObject(value);
            }
        }
        return new JSONArray();
    }

}
