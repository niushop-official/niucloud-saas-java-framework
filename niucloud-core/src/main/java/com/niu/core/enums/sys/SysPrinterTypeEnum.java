package com.niu.core.enums.sys;

import cn.hutool.json.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class SysPrinterTypeEnum {

    public static JSONArray getType() {
        return JsonModuleLoader.build().mergeResultSet(RequestUtils.siteId(), "printer/printer.json");
    }

    public static String getTypeName(String type) {
        return getType()
                .stream()
                .filter(o -> JSON.parseObject(o.toString()).getString("key").equals(type))
                .map(o -> JSON.parseObject(o.toString()).getString("title"))
                .findFirst()
                .orElse("");
    }

}
