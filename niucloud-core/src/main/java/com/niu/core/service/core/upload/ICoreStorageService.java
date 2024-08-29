package com.niu.core.service.core.upload;

import cn.hutool.json.JSONObject;
import com.niu.core.service.core.upload.vo.CoreStorAgeConfigVo;

import java.util.List;

/**
 * 上传服务层
 */
public interface ICoreStorageService {

    /**
     * 获取站点的云存储列表
     * @param siteId
     * @return
     */
    List<CoreStorAgeConfigVo> getStorageList(Integer siteId);

    /**
     * 获取站点的云存储配置
     * @param siteId
     * @return
     */
    JSONObject getStorageConfig(Integer siteId);

    /**
     * 获取当前启用的存储方式以及配置
     * @param siteId
     * @return
     */
    JSONObject getDefaultStorage(Integer siteId);

    /**
     * 通过存储方式获取配置
     * @param siteId
     * @param StorageType
     * @return
     */
    JSONObject getStorageByType(Integer siteId, String StorageType);
}
