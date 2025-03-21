package com.niu.core.service.core.wxoplatform.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.enums.sys.ConfigKeyEnum;
import com.niu.core.service.admin.wxoplatform.param.OplatformConfigParam;
import com.niu.core.service.admin.wxoplatform.vo.OplatformConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.core.service.core.sys.vo.SceneDomainVo;
import com.niu.core.service.core.wxoplatform.ICoreOplatformConfigService;
import com.niu.core.service.core.wxoplatform.vo.CoreOplatformStaticConfigVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.net.InetAddress;

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
        CoreOplatformStaticConfigVo coreOplatformStaticConfigVo = new CoreOplatformStaticConfigVo();
        coreOplatformStaticConfigVo.setAuthServeUrl(RequestUtils.getDomain(true) + "/adminapi/wxoplatform/server");
        coreOplatformStaticConfigVo.setMessageServeUrl(RequestUtils.getDomain(true) + "/adminapi/wxoplatform/message/$APPID$");
        coreOplatformStaticConfigVo.setAuthLaunchDomain(RequestUtils.getDomain(false));
        coreOplatformStaticConfigVo.setWechatAuthDomain(RequestUtils.getDomain(false));
        try {
            coreOplatformStaticConfigVo.setUploadIp(InetAddress.getByName("java.oss.niucloud.com").getHostAddress());
        } catch (Exception e) {
            coreOplatformStaticConfigVo.setUploadIp("");
        }
        return coreOplatformStaticConfigVo;
    }

    /**
     * 更新微信公众号配置信息
     *
     * @param oplatformConfigParam
     */
    @Override
    public void setOplatformConfig(OplatformConfigParam oplatformConfigParam) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("app_id", oplatformConfigParam.getAppId());
        jsonObject.set("app_secret", oplatformConfigParam.getAppSecret());
        jsonObject.set("token", oplatformConfigParam.getToken());
        jsonObject.set("aes_key", oplatformConfigParam.getAesKey());
        jsonObject.set("develop_app_id", oplatformConfigParam.getDevelopAppId());
        jsonObject.set("develop_upload_private_key", oplatformConfigParam.getDevelopUploadPrivateKey());
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), ConfigKeyEnum.WXOPLATFORM.getName(), jsonObject);
    }

    /**
     * 查询微信开放平台配置
     * @return
     */
    @Override
    public OplatformConfigVo getWxOplatformConfig() {
        OplatformConfigVo vo = new OplatformConfigVo();
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), ConfigKeyEnum.WXOPLATFORM.getName());
        if (ObjectUtil.isNotEmpty(config)) vo = JSONUtil.toBean(config, OplatformConfigVo.class);
        return vo;
    }

}
