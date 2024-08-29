package com.niu.core.service.core.sys;

import cn.hutool.json.JSONObject;

public interface ICoreScanService {

    /**
     * 扫码生成
     * @param siteId
     * @param action
     * @param data
     * @param expire
     * @return
     */
    String scan(Integer siteId, String action, JSONObject data, Integer expire);

    /**
     * 更新扫码信息
     * @param siteId
     * @param key
     * @param data
     */
    void actionByScan(Integer siteId, String key, JSONObject data);
}
