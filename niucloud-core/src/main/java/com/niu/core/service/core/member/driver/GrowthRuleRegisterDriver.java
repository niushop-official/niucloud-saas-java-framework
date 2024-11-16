package com.niu.core.service.core.member.driver;

import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GrowthRuleRegisterDriver {

    /**
     * 获取发放的成长值
     * @param siteId
     * @param config
     * @param key
     * @return
     */
    public Integer calculate(Integer siteId, String key, JSONObject config, Map<String, Object> data) {
        return config.getInt("growth", 0);
    }

    /**
     * 获取成长值规则内容
     * @param siteId
     * @param config
     * @param scene
     * @return
     */
    public Object content(Integer siteId, JSONObject config, String scene) {
        switch (scene) {
            case "admin":
                return "会员注册之后发放" + config.getStr("growth") + "成长值";
            default:
                return "";
        }
    }
}
