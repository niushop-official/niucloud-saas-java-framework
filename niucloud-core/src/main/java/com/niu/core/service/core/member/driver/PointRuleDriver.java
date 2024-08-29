package com.niu.core.service.core.member.driver;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class PointRuleDriver {

    /**
     * 获取发放的积分
     * @param siteId
     * @param config
     * @param key
     * @return
     */
    public Integer calculate(Integer siteId, String key, JSONObject config) {
        return config.getInt("point", 0);
    }
}
