package com.niu.core.service.core.diy;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

public interface ICoreDiyService {

    /**
     * 获取主题色字典
     * @return
     */
    JSONArray getThemeColorDict();

    /**
     * 获取默认主题
     * @param addon
     * @return
     */
    JSONObject getDefaultThemeColor(String addon);

    /**
     * 初始化主题
     */
    void initDefaultDiyTheme(Integer siteId);
}
