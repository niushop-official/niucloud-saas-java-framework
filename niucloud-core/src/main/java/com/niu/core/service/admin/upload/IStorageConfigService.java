package com.niu.core.service.admin.upload;

import cn.hutool.json.JSONObject;
import com.niu.core.service.core.upload.vo.CoreStorAgeConfigVo;

import java.util.List;

/**
 * 云存储服务层
 */
public interface IStorageConfigService {

    /**
     * 获取本地云存储列表
     * @return
     */
    List<CoreStorAgeConfigVo> getStorageList();


    /**
     * 查询云存储配置详情
     * @param storageType
     * @return
     */
    CoreStorAgeConfigVo getStorageConfig(String storageType);

    /**
     * 云存储配置 更新
     * @param storageType
     * @param storageData
     */
    void setStorageConfig(String storageType, JSONObject storageData);

}
