package com.niu.core.service.core.wxoplatform.impl;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.enums.sys.ConfigKeyEnum;
import com.niu.core.service.admin.wxoplatform.param.OplatformConfigParam;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.core.service.core.sys.vo.SceneDomainVo;
import com.niu.core.service.core.wxoplatform.ICoreOplatformConfigService;
import com.niu.core.service.core.wxoplatform.vo.CoreOplatformStaticConfigVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CoreOplatformStaticConfigServiceImpl implements ICoreOplatformConfigService {

    @Resource
    ICoreSysConfigService coreSysConfigService;
    @Resource
    ICoreConfigService coreConfigService;
    /**
     * 查询微信需要的静态信息
     *
     * @return
     */
    @Override
    public CoreOplatformStaticConfigVo getOplatformStaticInfo() {
        SceneDomainVo sceneDomainVo=coreSysConfigService.getSceneDomain(RequestUtils.defaultSiteId());
        String wapDomain=sceneDomainVo.getWapUrl();
        CoreOplatformStaticConfigVo coreOplatformStaticConfigVo=new CoreOplatformStaticConfigVo();
        coreOplatformStaticConfigVo.setAuthServeUrl(GlobalConfig.adminDomain+"/adminapi/wxoplatform/server");
        coreOplatformStaticConfigVo.setMessageServeUrl(GlobalConfig.adminDomain+"/adminapi/wxoplatform/message/$APPID$");
        coreOplatformStaticConfigVo.setAuthLaunchDomain(RequestUtils.getRequestDomain());
        coreOplatformStaticConfigVo.setWechatAuthDomain(wapDomain);
        coreOplatformStaticConfigVo.setUploadIp("");
        return coreOplatformStaticConfigVo;
    }

    /**
     * 更新微信公众号配置信息
     *
     * @param oplatformConfigParam
     */
    @Override
    public void setOplatformConfig(OplatformConfigParam oplatformConfigParam) {
        JSONObject jsonObject=new JSONObject();
        jsonObject.set("app_id", oplatformConfigParam.getAppId());
        jsonObject.set("app_secret", oplatformConfigParam.getAppSecret());
        jsonObject.set("token", oplatformConfigParam.getToken());
        jsonObject.set("aes_key", oplatformConfigParam.getAesKey());
        jsonObject.set("develop_app_id", oplatformConfigParam.getDevelopAppId());
        jsonObject.set("develop_upload_private_key", oplatformConfigParam.getDevelopUploadPrivateKey());
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), ConfigKeyEnum.WXOPLATFORM.getName(), jsonObject);
    }
}
