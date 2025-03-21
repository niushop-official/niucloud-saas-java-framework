package com.niu.core.service.core.sys;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.entity.sys.SysConfig;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;

public interface ICoreConfigService {

    /**
     * 获取配置项
     *
     * @param siteId 站点id
     * @param key    关键字
     * @return
     */
    CoreSysConfigVo getConfig(Integer siteId, String key);

    /**
     * 获取系统配置项 返回values
     *
     * @param siteId
     * @param key
     * @return
     */
    JSONObject getConfigValue(Integer siteId, String key);

    /**
     * 获取系统配置项 返回values
     *
     * @param siteId
     * @param key
     * @return
     */
    JSONArray getConfigArrayValue(Integer siteId, String key);

    /**
     * 修改系统配置项
     *
     * @param siteId
     * @param key
     * @param valueJson
     */
    void setConfig(Integer siteId, String key, JSON valueJson);

    /**
     * 清除缓存
     */
    void cacheClear();
}
