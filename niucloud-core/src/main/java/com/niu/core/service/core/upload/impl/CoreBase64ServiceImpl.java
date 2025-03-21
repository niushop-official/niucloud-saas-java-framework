package com.niu.core.service.core.upload.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.upload.IUploadProvider;
import com.niu.core.common.component.upload.factory.UploadProviderFactory;
import com.niu.core.common.component.upload.model.Base64Model;
import com.niu.core.common.utils.date.DateFormatUtils;
import com.niu.core.enums.sys.StorageEnum;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.upload.ICoreBase64Service;
import org.springframework.stereotype.Service;

@Service
public class CoreBase64ServiceImpl implements ICoreBase64Service {

    @Override
    public String image(String content, Integer siteId) {
        // 获取站点的上传配置
        ICoreConfigService coreConfigService = SpringContext.bean(ICoreConfigService.class);
        JSONObject rootJSONObject = coreConfigService.getConfigValue(siteId, "STORAGE");
        String defaultValue = rootJSONObject.getStr("default", StorageEnum.LOCAL.getCode());
        JSONObject configObject = rootJSONObject.getJSONObject(defaultValue);
        // 根据配置初始化
        IUploadProvider uploadProvider = UploadProviderFactory.createAndInit(defaultValue, configObject);

        Base64Model base64Model = new Base64Model();
        base64Model.setContent(content);
        base64Model.setUploadFilePath("attachment/image/" + siteId + "/" + DateFormatUtils.getUploadFormat() + "/");
        base64Model.setUploadFileName(SecureUtil.md5(content) + ".jpg");
        return uploadProvider.getAccessUrl(uploadProvider.base64(base64Model));
    }
}
