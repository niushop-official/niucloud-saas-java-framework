package com.niu.core.service.admin.wxoplatform.impl;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.sys.ConfigKeyEnum;
import com.niu.core.service.admin.wxoplatform.IOplatformConfigService;
import com.niu.core.service.admin.wxoplatform.param.OplatformConfigParam;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.wxoplatform.ICoreOplatformConfigService;
import com.niu.core.service.core.wxoplatform.vo.CoreOplatformStaticConfigVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class OplatformConfigServiceImpl implements IOplatformConfigService {

    @Resource
    ICoreOplatformConfigService coreOplatformConfigService;

    @Resource
    ICoreConfigService coreConfigService;

    /**
     * 查询微信需要的静态信息
     *
     * @return
     */
    @Override
    public CoreOplatformStaticConfigVo getOplatformStaticInfo() {
        return coreOplatformConfigService.getOplatformStaticInfo();
    }

    /**
     * 获取微信公众号配置
     *
     * @return
     */
    @Override
    public JSONObject getWxOplatformConfig() {
        JSONObject jsonObject=coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), ConfigKeyEnum.WXOPLATFORM.getName());
        JSONObject configJsonObject=new JSONObject();
        configJsonObject.set("app_id", jsonObject.getStr("app_id", ""));
        configJsonObject.set("app_secret", jsonObject.getStr("app_secret", ""));
        configJsonObject.set("token", jsonObject.getStr("token", ""));
        configJsonObject.set("aes_key", jsonObject.getStr("aes_key", ""));
        configJsonObject.set("develop_app_id", jsonObject.getStr("develop_app_id", ""));
        configJsonObject.set("develop_upload_private_key", jsonObject.getStr("develop_upload_private_key", ""));
        return configJsonObject;
    }

    /**
     * 更新微信开放平台配置
     *
     * @param oplatformConfigParam
     */
    @Override
    public void setWxOplatformConfig(OplatformConfigParam oplatformConfigParam) {
        coreOplatformConfigService.setOplatformConfig(oplatformConfigParam);
    }
}
