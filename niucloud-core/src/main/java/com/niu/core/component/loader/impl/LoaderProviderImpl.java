package com.niu.core.component.loader.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.component.loader.AbstractLoaderProvider;
import com.niu.core.component.loader.ILoaderProvider;
import com.niu.core.config.dataization.AddonModuleContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class LoaderProviderImpl extends AbstractLoaderProvider implements ILoaderProvider {

    @Override
    public JSON loadJSON(String addon, String location) {
        JSON json = null;
        String jsonString = AddonModuleContext.readResourceAsStreamToText(addon, "loader/" + location);
        if (StrUtil.isNotEmpty(jsonString)) {
            try {
                json = JSONUtil.parse(jsonString);
            } catch (Exception e) {
                log.error(addon + "=>" + location + ",json context error.");
                e.printStackTrace();
            }
        }
        return json;
    }

    @Override
    public JSONObject loadJSONObject(String addon, String location) {
        JSONObject jsonObject = new JSONObject();
        String jsonString = AddonModuleContext.readResourceAsStreamToText(addon, "loader/" + location);
        if (StrUtil.isNotEmpty(jsonString)) {
            jsonString = jsonString.trim();
            try {
                jsonObject = JSONUtil.parseObj(jsonString);
            } catch (Exception e) {
                log.error(addon + "=>" + location + ",json context error.");
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    @Override
    public JSONArray loadJSONArray(String addon, String location) {
        JSONArray jsonArray = new JSONArray();
        String jsonString = AddonModuleContext.readResourceAsStreamToText(addon, "loader/" + location);
        if (StrUtil.isNotEmpty(jsonString)) {
            jsonString = jsonString.trim();
            try {
                jsonArray = JSONUtil.parseArray(jsonString);
            } catch (Exception e) {
                log.error(addon + "=>" + location + ",json context error.");
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    @Override
    public JSONObject mergeJSONObject(String location) {
        // 读取JSON模块到集合
        Set<JSONObject> jsonContentSet = new HashSet<>();
        Set<String> addonModules = AddonModuleContext.getAddonModules();
        for (String addonModule : addonModules) {
            String jsonString = AddonModuleContext.readResourceAsStreamToText(addonModule, "loader/" + location);
            if (StrUtil.isNotEmpty(jsonString)) {
                jsonString = jsonString.trim();
                try {
                    if (jsonString.startsWith("{")) {
                        jsonContentSet.add(JSONUtil.parseObj(jsonString));
                    }
                } catch (Exception e) {
                    log.error(addonModule + "=>" + location + " json context error.");
                    e.printStackTrace();
                }
            }
        }
        // 将集合结果合并成一个JSON对象
        JSONObject jsonObjectResult = new JSONObject();
        try {
            jsonObjectResult = super.mergeJsonForAttribute(jsonContentSet);
        } catch (Exception e) {
            log.error("JSON合并异常:" + e.getMessage());
            e.printStackTrace();
        }
        return jsonObjectResult;
    }


    @Override
    public JSONArray mergeJSONArray(String location) {
        JSONArray jsonArrayResult = new JSONArray();
        Set<String> addonModules = AddonModuleContext.getAddonModules();
        for (String addonModule : addonModules) {
            String jsonString = AddonModuleContext.readResourceAsStreamToText(addonModule, "loader/" + location);
            if (StrUtil.isNotEmpty(jsonString)) {
                jsonString = jsonString.trim();
                try {
                    if (jsonString.startsWith("[")) {
                        JSONArray jsonArray = JSONUtil.parseArray(jsonString);
                        jsonArray.stream().forEach(json -> jsonArrayResult.add(json));
                    }
                } catch (Exception e) {
                    log.error(addonModule + "=>" + location + " json context error.");
                    e.printStackTrace();
                }
            }
        }
        return jsonArrayResult;
    }

}
