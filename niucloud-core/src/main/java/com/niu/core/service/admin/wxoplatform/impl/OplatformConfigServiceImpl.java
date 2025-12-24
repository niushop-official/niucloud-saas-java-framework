package com.niu.core.service.admin.wxoplatform.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.enums.sys.ConfigKeyEnum;
import com.niu.core.service.admin.wxoplatform.IOplatformConfigService;
import com.niu.core.service.admin.wxoplatform.param.OplatformConfigParam;
import com.niu.core.service.admin.wxoplatform.vo.OplatformConfigVo;
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
    public OplatformConfigVo getWxOplatformConfig() {
        OplatformConfigVo configVo = coreOplatformConfigService.getWxOplatformConfig();
        if (ObjectUtil.isNotEmpty(configVo.getAppSecret())) configVo.setAppSecret(StringUtils.ENCRYPTSTR);
        if (ObjectUtil.isNotEmpty(configVo.getAesKey())) configVo.setAesKey(StringUtils.ENCRYPTSTR);
        return configVo;
    }

    /**
     * 更新微信开放平台配置
     *
     * @param oplatformConfigParam
     */
    @Override
    public void setWxOplatformConfig(OplatformConfigParam oplatformConfigParam) {
        OplatformConfigVo configVo = coreOplatformConfigService.getWxOplatformConfig();
        if (oplatformConfigParam.getAppSecret().equals(StringUtils.ENCRYPTSTR)) oplatformConfigParam.setAppSecret(configVo.getAppSecret());
        if (oplatformConfigParam.getAesKey().equals(StringUtils.ENCRYPTSTR)) oplatformConfigParam.setAesKey(configVo.getAesKey());
        coreOplatformConfigService.setOplatformConfig(oplatformConfigParam);
    }
}
