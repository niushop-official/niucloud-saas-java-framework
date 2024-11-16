package com.niu.core.service.core.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.service.core.sys.ICoreScanService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CoreScanServiceImpl implements ICoreScanService {

    @Resource
    private Cached cached;

    /**
     * 扫码生成
     * @param siteId
     * @param action
     * @param data
     * @param expire
     * @return
     */
    public String scan(Integer siteId, String action, JSONObject data, Integer expire) {
        String key = RandomUtil.randomString(32);
        data.set("status", "wait");
        data.set("is_scan", false);
        data.set("action", action);
        data.set("expire", DateUtils.timestampToString(System.currentTimeMillis() / 1000 + expire));
        cached.put("scan_" + key, data.toString(), expire);
        return key;
    }

    /**
     * 更新扫码信息
     * @param siteId
     * @param key
     * @param data
     */
    public void actionByScan(Integer siteId, String key, JSONObject data) {
        String cache = (String) cached.get("scan_" + key);
        if (ObjectUtil.isNotNull(cache) && !cache.isEmpty()) {
            JSONObject cacheData = JSONUtil.parseObj(cache);
            cacheData.set("is_scan", true);
            cacheData = JsonModuleLoader.deepMerge(cacheData, data);
            cached.put("scan_" + key, cacheData.toString());
        }
    }
}
