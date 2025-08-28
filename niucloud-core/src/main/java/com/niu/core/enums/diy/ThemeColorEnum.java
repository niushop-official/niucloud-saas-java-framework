package com.niu.core.enums.diy;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ThemeColorEnum {


    /**
     * 获取所有主题色
     * @return
     */
    public static JSONArray getThemeColor() {
        try {
            JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
            return jsonModuleLoader.mergeContentToArray("diy/theme_color.json");
        } catch (Exception e) {
            log.error("加载主题色文件时发生错误: {}", e.getMessage());
            return new JSONArray();
        }
    }

    /**
     * 获取插件主题色
     * @param addonName
     * @return
     */
    public static JSONObject getAddonThemeColor(String addonName) {
        if (addonName == null || addonName.isEmpty()) {
            log.error("插件名不能为空");
            return new JSONObject();
        }

        JSONArray themeColor = getThemeColor();
        if (ObjectUtil.isNotEmpty(themeColor)) {
            for (int i = 0; i < themeColor.size(); i++) {
                JSONObject addonThemeColor = themeColor.getJSONObject(i);
                if ((addonName + "_theme").equals(addonThemeColor.getStr("theme_type"))) {
                    return addonThemeColor;
                }
            }
        }
        log.warn("未找到插件名为 {} 的主题色", addonName);
        return new JSONObject();
    }
}
