package com.niu.core.common.utils.json;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.config.dataization.AddonModuleContext;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.site.ICoreSiteService;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

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
     * 合并结果集
     * eg:  [{name1:value1}]  [{name2:value2}] 合并后： [{name1:value1},{name2:value2}]
     *
     * @param jsonFile
     * @return
     */
    public JSONArray mergeResultSet(Integer siteId, String jsonFile) {
        final JSONArray jsonArrayResult = new JSONArray();
        List<String> addonModules = getSiteAddonModules(siteId);
        for (String addonModule : addonModules) {
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
    public JSONObject mergeResultElement(Integer siteId, String jsonFile) {
        Set<JSONObject> jsonContentSet = new HashSet<>();
        List<String> addonModules = getSiteAddonModules(siteId);
        log.info("JSON addonModules -> " + addonModules);
        for (String addonModule : addonModules) {
            String jsonString = AddonModuleContext.readResourceAsStreamToText(addonModule, "loader/" + jsonFile);
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
     * 获取站点插件
     * @param siteId
     * @return
     */
    public List<String> getSiteAddonModules(Integer siteId) {
        List<String> addonModules = new LinkedList<>();
        addonModules.add("core");
        if (ObjectUtil.isNull(siteId) || siteId == 0) {
            addonModules.addAll(SpringContext.bean(ICoreAddonService.class).getInstallAddonList().keySet().stream().toList());
        } else {
            addonModules.addAll(SpringContext.bean(ICoreSiteService.class).getAddonKeysBySiteId(siteId));
        }
        return addonModules;
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
            for (String key : jsonObject.keySet()) {
                Object value = jsonObject.get(key);
                if (targetMap.containsKey(key)) {
                    // 如果targetMap中已经有该key，且value是JSONObject，则递归合并
                    Object existingValue = targetMap.get(key);
                    if (existingValue instanceof JSONObject && value instanceof JSONObject) {
                        JSONObject merged = mergeJsonObjects((JSONObject) existingValue, (JSONObject) value);
                        targetMap.put(key, merged);
                    } else {
                        // 否则，用新的value覆盖（或者根据需求处理，这里直接覆盖）
                        targetMap.put(key, value);
                    }
                } else {
                    targetMap.put(key, value);
                }
            }
        }
        return targetMap;
    }

    // 递归合并两个JSONObject
    private JSONObject mergeJsonObjects(JSONObject obj1, JSONObject obj2) {
        JSONObject result = new JSONObject();
        // 先添加obj1的所有键值对
        for (String key : obj1.keySet()) {
            result.put(key, obj1.get(key));
        }
        // 合并obj2的键值对
        for (String key : obj2.keySet()) {
            Object value2 = obj2.get(key);
            if (result.containsKey(key)) {
                Object value1 = result.get(key);
                // 如果两个值都是JSONObject，则递归合并
                if (value1 instanceof JSONObject && value2 instanceof JSONObject) {
                    JSONObject merged = mergeJsonObjects((JSONObject) value1, (JSONObject) value2);
                    result.put(key, merged);
                } else {
                    // 否则，用obj2的值覆盖
                    result.put(key, value2);
                }
            } else {
                result.put(key, value2);
            }
        }
        return result;
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
