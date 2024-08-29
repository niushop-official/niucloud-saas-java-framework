package com.niu.core.service.core.member.driver;

import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class GrowthRuleDriver {

    /**
     * 获取发放的成长值
     * @param siteId
     * @param config
     * @param key
     * @return
     */
    public Integer calculate(Integer siteId, String key, JSONObject config) {
        return config.getInt("growth", 0);
    }
}
