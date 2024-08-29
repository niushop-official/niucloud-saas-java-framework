package com.niu.core.service.core.weapp.helper;

import cn.hutool.json.JSONObject;
import com.niu.core.component.context.SpringContext;
import com.niu.core.service.core.notice.ICoreNoticeSmsLogService;
import com.niu.core.service.core.sys.ICoreConfigService;

import java.util.Map;

public class CoreWeappHelper {

    /**
     * 根据站点<系统配置表>发送消息
     *
     * @param siteId
     * @param weappKey
     * @param dataMap
     * @return
     */
    public static Object send(Integer siteId, String weappKey, Map<String, Object> dataMap) {
        JSONObject jsonObject = getWeappConfig(siteId);
        // 默认使用的短信名称
        String smsName = jsonObject.getStr("default");
        // 根据名称获取配置
        JSONObject configObject = jsonObject.getJSONObject(smsName);

        return null;
    }

    /**
     * 获取站点的短信配置
     *
     * @return
     */
    public static JSONObject getWeappConfig(Integer siteId) {
        ICoreConfigService coreConfigService = SpringContext.bean(ICoreConfigService.class);
        JSONObject smsObject = coreConfigService.getConfigValue(siteId, "weapp");
        return smsObject;
    }

}
