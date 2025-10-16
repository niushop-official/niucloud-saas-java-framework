package com.niu.core.service.admin.upload.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.loader.upload.UploadLoader;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.enums.sys.FileEnum;
import com.niu.core.enums.sys.StorageEnum;
import com.niu.core.service.admin.upload.IStorageConfigService;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.upload.ICoreStorageService;
import com.niu.core.service.core.upload.vo.CoreStorAgeConfigVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

@Service
public class StorageConfigServiceImpl implements IStorageConfigService {

    @Resource
    ICoreStorageService coreStorageService;
    @Resource
    ICoreConfigService coreConfigService;

    /**
     * 获取本地云存储列表
     *
     * @return
     */
    @Override
    public List<CoreStorAgeConfigVo> getStorageList() {
        return coreStorageService.getStorageList(RequestUtils.siteId());
    }

    /**
     * 查询云存储配置详情
     *
     * @param storageType
     * @return
     */
    @Override
    public CoreStorAgeConfigVo getStorageConfig(String storageType) {

        JSONObject storageTypeList = UploadLoader.getType();
        if (ObjectUtil.isNull(storageTypeList.get(storageType))) {
            throw new AdminException("OSS_TYPE_NOT_EXIST");
        }
        /**
         * 获取配置
         */
        JSONObject storageConfig = coreStorageService.getStorageConfig(RequestUtils.siteId());

        JSONObject storageValues = JSONUtil.parseObj(storageTypeList.get(storageType));
        CoreStorAgeConfigVo coreStorAgeConfigVo = new CoreStorAgeConfigVo();
        coreStorAgeConfigVo.setStorageType(storageType);
        coreStorAgeConfigVo.setIsUse(storageType.equals(storageConfig.get("default")) ? StorageEnum.ON.getCode() : StorageEnum.OFF.getCode());
        coreStorAgeConfigVo.setName(storageValues.get("name").toString());
        coreStorAgeConfigVo.setComponent(storageValues.get("component").toString());

        JSONArray encryptParams = ObjectUtil.defaultIfNull(storageValues.getJSONArray("encrypt_params"), new JSONArray());

        JSONObject params = new JSONObject();
        if (ObjectUtil.isNotNull(storageValues.get("params"))) {
            JSONObject valuesParams = JSONUtil.parseObj(storageValues.get("params"));
            JSONObject configParams = new JSONObject();
            if (ObjectUtil.isNotNull(storageConfig.get(storageType))) {
                configParams = JSONUtil.parseObj(storageConfig.get(storageType));
            }
            for (String paramsKey : valuesParams.keySet()) {
                JSONObject itemParam = new JSONObject();
                String paramsValues = valuesParams.get(paramsKey).toString();
                itemParam.set("name", paramsValues);
                String value = configParams.getStr(paramsKey);
                if (encryptParams.contains(paramsKey) && ObjectUtil.isNotEmpty(value)) {
                    value = StringUtils.hide(value, 0, value.length());
                }
                itemParam.set("value", value);
                params.set(paramsKey, itemParam);
            }
        }
        coreStorAgeConfigVo.setParams(params);
        return coreStorAgeConfigVo;
    }

    /**
     * 云存储配置 更新
     *
     * @param storageType
     * @param storageData
     */
    @Override
    public void setStorageConfig(String storageType, JSONObject storageData) {
        JSONObject storageTypeList = UploadLoader.getType();
        if (ObjectUtil.isNull(storageTypeList.get(storageType))) {
            throw new AdminException("云存储类型不存在");
        }
        if (!storageType.equals(FileEnum.LOCAL.getCode())) {
            String domain = storageData.getStr("domain");
            if (domain.indexOf("http://") < 0 && domain.indexOf("https://") < 0) {
                throw new AdminException("空间域名请补全http://或https://");
            }
        }
        /**
         * 获取配置
         */
        JSONObject storageConfig = coreStorageService.getStorageConfig(RequestUtils.siteId());

        JSONObject storageValues = JSONUtil.parseObj(storageTypeList.get(storageType));
        if (storageData.getInt("is_use") == 1) {
            storageConfig.set("default", storageType);
        } else {
            if (storageData.getStr("storage_type").equals(storageConfig.get("default"))) {
                storageConfig.set("default", "");
            }
        }

        JSONObject configParams = new JSONObject();
        if (ObjectUtil.isNotNull(storageConfig.get(storageType))) {
            configParams = JSONUtil.parseObj(storageConfig.get(storageType));
        }

        if (ObjectUtil.isNotNull(storageValues.get("params"))) {
            JSONObject valuesParams = JSONUtil.parseObj(storageValues.get("params"));
            for (String paramsKey : valuesParams.keySet()) {
                String value = storageData.getStr(paramsKey);
                if (!value.contains("*")) {
                    configParams.set(paramsKey, value);
                }
            }
        }

        storageConfig.set(storageType, configParams);

        coreConfigService.setConfig(RequestUtils.siteId(), "STORAGE", storageConfig);

    }
}
