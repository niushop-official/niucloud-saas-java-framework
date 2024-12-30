package com.niu.recharge.enums;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.json.JsonModuleLoader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PackageGiftEnum {
    public static JSONArray getPackageGiftEnum() {
        JSONObject entries = JsonModuleLoader.build().mergeResultElement("recharge/package_gift.json");
        log.info("======PackageGiftEnum entries: {}", entries.toString());

        JSONArray jsonArray = new JSONArray();
        entries.forEach((k, v) -> {
            JSONObject value = JSONUtil.parseObj(v);
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("key", k);
            jsonObject.set("name", value.get("name"));
            jsonObject.set("value", value.get("value"));
            jsonObject.set("edit_component", value.get("edit_component"));
            jsonObject.set("detail_component", value.get("detail_component"));
            jsonObject.set("sort", value.get("sort"));
            jsonArray.add(jsonObject);
        });
        return jsonArray;
    }
}
