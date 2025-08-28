package com.niu.core.service.core.upload.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.loader.upload.UploadLoader;
import com.niu.core.enums.sys.FileEnum;
import com.niu.core.enums.sys.StorageEnum;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.upload.ICoreStorageService;
import com.niu.core.service.core.upload.vo.CoreStorAgeConfigVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class CoreStorageServiceImpl implements ICoreStorageService {

    @Resource
    ICoreConfigService coreConfigService;

    /**
     * 获取站点的云存储列表
     *
     * @param siteId
     * @return
     */
    @Override
    public List<CoreStorAgeConfigVo> getStorageList(Integer siteId) {
        /**
         * 获取配置
         */
        JSONObject storageConfig = getStorageConfig(siteId);

        JSONObject storageTypeList = UploadLoader.getType();
        List<CoreStorAgeConfigVo> coreStorAgeConfigVoList = new ArrayList<>();
        for (String key : storageTypeList.keySet()) {
            JSONObject storageValues = JSONUtil.parseObj(storageTypeList.get(key));
            CoreStorAgeConfigVo coreStorAgeConfigVo = new CoreStorAgeConfigVo();
            coreStorAgeConfigVo.setStorageType(key);
            coreStorAgeConfigVo.setIsUse(key.equals(storageConfig.get("default")) ? StorageEnum.ON.getCode() : StorageEnum.OFF.getCode());
            coreStorAgeConfigVo.setName(storageValues.get("name").toString());
            coreStorAgeConfigVo.setComponent(storageValues.get("component").toString());
            JSONObject params = new JSONObject();
            if (ObjectUtil.isNotNull(storageValues.get("params"))) {
                JSONObject valuesParams = JSONUtil.parseObj(storageValues.get("params"));
                JSONObject configParams = new JSONObject();
                if (ObjectUtil.isNotNull(storageConfig.get(key))) {
                    configParams = JSONUtil.parseObj(storageConfig.get(key));
                }
                for (String paramsKey : valuesParams.keySet()) {
                    JSONObject itemParam = new JSONObject();
                    String paramsValues = valuesParams.get(paramsKey).toString();
                    itemParam.set("name", paramsValues);
                    itemParam.set("value", configParams.get(paramsKey));
                    params.set(paramsKey, itemParam);
                }
                params.set("config_params", configParams);
            }
            coreStorAgeConfigVo.setParams(params);
            coreStorAgeConfigVoList.add(coreStorAgeConfigVo);
        }
        return coreStorAgeConfigVoList;
    }

    /**
     * 获取站点的云存储配置
     *
     * @param siteId
     * @return
     */
    @Override
    public JSONObject getStorageConfig(Integer siteId) {
        JSONObject jsonObject = coreConfigService.getConfigValue(siteId, "STORAGE");
        if (ObjectUtil.isNull(jsonObject) || ObjectUtil.isEmpty(jsonObject)) {
            jsonObject = new JSONObject();
            jsonObject.set("default", FileEnum.LOCAL.getCode());
        }
        return jsonObject;
    }

    /**
     * 获取当前启用的存储方式以及配置
     *
     * @param siteId
     * @return
     */
    public JSONObject getDefaultStorage(Integer siteId) {
        List<CoreStorAgeConfigVo> storageList = this.getStorageList(siteId);
        JSONObject config = new JSONObject();
        for (CoreStorAgeConfigVo item : storageList) {
            if (item.getIsUse().equals(StorageEnum.ON.getCode())) {
                config = item.getParams();
                config.put("storage_type", item.getStorageType());
            }
        }
        return config;
    }

    public JSONObject getStorageByType(Integer siteId, String StorageType) {
        List<CoreStorAgeConfigVo> storageList = this.getStorageList(siteId);
        JSONObject config = new JSONObject();
        for (CoreStorAgeConfigVo item : storageList) {
            if (item.getIsUse().equals(StorageEnum.ON.getCode()) && item.getStorageType().equals(StorageType)) {
                config = item.getParams();
                config.put("storage_type", item.getStorageType());
            }
        }
        return config;
    }

}
