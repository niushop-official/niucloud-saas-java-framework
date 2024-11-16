package com.niu.core.common.utils.json;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.config.dataization.AddonModuleContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class JsonModuleLoader {

    private static JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();

    /**
     * @param addon
     * @param jsonFile
     * @return
     */
    public JSONArray findContentToArray(String addon, String jsonFile) {
        JSONArray jsonArrayResult = new JSONArray();
        String jsonString = AddonModuleContext.readResourceAsStreamToText(addon, "loader/" + jsonFile);
        log.info("JSON => " + addon + " loader/" + jsonFile + " , result => " + jsonString);
        if (StrUtil.isNotEmpty(jsonString)) {
            jsonString = jsonString.trim();
            try {
                if (jsonString.startsWith("[")) {
                    JSONArray jsonArray = JSONUtil.parseArray(jsonString);
                    jsonArrayResult.add(jsonArray);
                } else {
                    JSONObject jsonObject = JSONUtil.parseObj(jsonString);
                    jsonArrayResult.add(jsonObject);
                }
            } catch (Exception e) {
                log.error(addon + "=>" + jsonFile + " json context error.");
                e.printStackTrace();
            }
        }
        return jsonArrayResult;
    }

    /**
     * 合并到数组
     * eg:  {name1:value1}   [{name2:value2}]   合并后： [{name1:value1},[{name2:value2}]]
     *
     * @param jsonFile
     * @return
     */
    public JSONArray mergeContentToArray(String jsonFile) {
        JSONArray jsonArrayResult = new JSONArray();
        Set<String> addonModules = AddonModuleContext.getAddonModules();
        for (String addonModule : addonModules) {
            String jsonString = AddonModuleContext.readResourceAsStreamToText(addonModule, "loader/" + jsonFile);
            log.info("JSON => " + addonModule + " loader/" + jsonFile + " , result => " + jsonString);
            if (StrUtil.isNotEmpty(jsonString)) {
                jsonString = jsonString.trim();
                try {
                    if (jsonString.startsWith("[")) {
                        JSONArray jsonArray = JSONUtil.parseArray(jsonString);
                        jsonArrayResult.add(jsonArray);
                    } else {
                        JSONObject jsonObject = JSONUtil.parseObj(jsonString);
                        jsonArrayResult.add(jsonObject);
                    }
                } catch (Exception e) {
                    log.error(addonModule + "=>" + jsonFile + " json context error.");
                    e.printStackTrace();
                }
            }
        }
        return jsonArrayResult;
    }

    /**
     * 合并结果集
     *
     * @param jsonFile
     * @return
     */
    public JSONArray findResultSet(String addon, String jsonFile) {
        final JSONArray jsonArrayResult = new JSONArray();
        String jsonString = AddonModuleContext.readResourceAsStreamToText(addon, "loader/" + jsonFile);
        log.info("JSON => " + addon + " loader/" + jsonFile + " , result => " + jsonString);
        if (StrUtil.isNotEmpty(jsonString)) {
            jsonString = jsonString.trim();
            try {
                if (jsonString.startsWith("[")) {
                    JSONArray jsonArray = JSONUtil.parseArray(jsonString);
                    jsonArray.stream().forEach(e -> jsonArrayResult.add(e));
                }
            } catch (Exception e) {
                log.error(addon + "=>" + jsonFile + " json context error.");
                e.printStackTrace();
            }
        }
        return jsonArrayResult;
    }


    /**
     * 合并结果集
     *
     * @param jsonFile
     * @return
     */
    public JSONArray getResultSet(String addonModule, String jsonFile) {
        final JSONArray jsonArrayResult = new JSONArray();
        String jsonString = AddonModuleContext.readResourceAsStreamToText(addonModule, "loader/" + jsonFile);
        log.info("JSON => " + addonModule + " loader/" + jsonFile + " , result => " + jsonString);
        if (StrUtil.isNotEmpty(jsonString)) {
            jsonString = jsonString.trim();
            try {
                if (jsonString.startsWith("[")) {
                    JSONArray jsonArray = JSONUtil.parseArray(jsonString);
                    jsonArray.stream().forEach(e -> jsonArrayResult.add(e));
                }
            } catch (Exception e) {
                log.error(addonModule + "=>" + jsonFile + " json context error.");
                e.printStackTrace();
            }
        }
        return jsonArrayResult;
    }


    /**
     * 合并结果集
     * eg:  [{name1:value1}]  [{name2:value2}] 合并后： [{name1:value1},{name2:value2}]
     *
     * @param jsonFile
     * @return
     */
    public JSONArray mergeResultSet(String jsonFile) {
        final JSONArray jsonArrayResult = new JSONArray();
        Set<String> addonModules = AddonModuleContext.getAddonModules();
        Set<String> newAddonModules = new HashSet<>();
        if (addonModules != null || addonModules.size() <= 0 || !addonModules.contains("core")) {
            newAddonModules.add("core");
        }
        newAddonModules.addAll(addonModules);
        for (String addonModule : newAddonModules) {
            String jsonString = AddonModuleContext.readResourceAsStreamToText(addonModule, "loader/" + jsonFile);
            log.info("JSON => " + addonModule + " loader/" + jsonFile + " , result => " + jsonString);
            if (StrUtil.isNotEmpty(jsonString)) {
                jsonString = jsonString.trim();
                try {
                    if (jsonString.startsWith("[")) {
                        JSONArray jsonArray = JSONUtil.parseArray(jsonString);
                        jsonArray.stream().forEach(e -> jsonArrayResult.add(e));
                    }
                } catch (Exception e) {
                    log.error(addonModule + "=>" + jsonFile + " json context error.");
                    e.printStackTrace();
                }
            }
        }
        return jsonArrayResult;
    }

    /**
     * 合并结果元素
     * eg:  {demo1:{name1:value1}}  {demo2:{name2:value2}}  合并后：{demo1:{name1:value1},demo2:{name2:value2}}
     *
     * @param jsonFile
     * @return
     */
    public JSONObject mergeResultElement(String jsonFile) {
        Set<JSONObject> jsonContentSet = new HashSet<>();
        Set<String> addonModules = AddonModuleContext.getAddonModules();
        log.info("JSON addonModules -> " + addonModules);
        for (String addonModule : addonModules) {
            String jsonString = AddonModuleContext.readResourceAsStreamToText(addonModule, "loader/" + jsonFile);
            log.info("JSON => " + addonModule + " loader/" + jsonFile + " , result => " + jsonString);
            if (StrUtil.isNotEmpty(jsonString)) {
                jsonString = jsonString.trim();
                try {
                    if (jsonString.startsWith("{")) {
                        jsonContentSet.add(JSONUtil.parseObj(jsonString));
                    }
                } catch (Exception e) {
                    log.error(addonModule + "=>" + jsonFile + " json context error.");
                    e.printStackTrace();
                }
            }
        }
        JSONObject jsonObjectResult = new JSONObject();
        try {
            jsonObjectResult = this.mergeJsonForAttribute(jsonContentSet);
        } catch (Exception e) {
            log.error("JSON合并异常:" + e.getMessage());
            e.printStackTrace();
        }
        return jsonObjectResult;
    }


    /**
     * 合并结果元素
     * eg:  {demo1:{name1:value1}}  {demo2:{name2:value2}}  合并后：{demo1:{name1:value1},demo2:{name2:value2}}
     *
     * @param jsonFile
     * @return
     */
    public JSONObject getResultElement(String addonModule, String jsonFile) {
        Set<JSONObject> jsonContentSet = new HashSet<>();
        String jsonString = AddonModuleContext.readResourceAsStreamToText(addonModule, "loader/" + jsonFile);
        log.info("JSON => " + addonModule + " loader/" + jsonFile + " , result => " + jsonString);
        if (StrUtil.isNotEmpty(jsonString)) {
            jsonString = jsonString.trim();
            try {
                if (jsonString.startsWith("{")) {
                    jsonContentSet.add(JSONUtil.parseObj(jsonString));
                }
            } catch (Exception e) {
                log.error(addonModule + "=>" + jsonFile + " json context error.");
                e.printStackTrace();
            }
        }
        JSONObject jsonObjectResult = new JSONObject();
        try {
            jsonObjectResult = this.mergeJsonForAttribute(jsonContentSet);
        } catch (Exception e) {
            log.error("JSON合并异常:" + e.getMessage());
            e.printStackTrace();
        }
        return jsonObjectResult;
    }

    /**
     * @param jsonObjectList
     * @return
     */
    private JSONObject mergeJsonForAttribute(Collection<JSONObject> jsonObjectList) {
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

    /**
     * @param jsonFile
     * @return
     */
    public static JSONArray load(String jsonFile) {
        return jsonModuleLoader.mergeContentToArray(jsonFile);
    }

    /**
     * @return
     */
    public static JsonModuleLoader build() {
        return jsonModuleLoader;
    }


    /**
     * 用于测试
     */
    public static void test() {
        JSONArray jsonResult = JsonModuleLoader.load("account_type.json");
        System.out.println("测试模块：" + "account_type.json");
        System.out.println("读取内容：" + jsonResult);
    }

    /**
     * 递归合并两个JSONObject
     *
     * @param target 第一个JSONObject
     * @param source 第二个JSONObject，其值将覆盖target中相同key的值
     * @return 合并后的JSONObject
     */
    public static JSONObject deepMerge(JSONObject target, JSONObject source) {
        for (String key : source.keySet()) {
            Object value = source.get(key);
            if (value instanceof JSONObject) {
                // 如果值是JSONObject，则递归合并
                JSONObject subTarget = target.getJSONObject(key);
                if (subTarget != null) deepMerge(subTarget, (JSONObject) value);
                else target.put(key, value);
            } else {
                // 否则，直接覆盖或添加值
                target.put(key, value);
            }
        }
        return target;
    }
}
