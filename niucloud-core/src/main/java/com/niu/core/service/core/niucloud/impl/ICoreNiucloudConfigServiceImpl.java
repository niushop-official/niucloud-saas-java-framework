package com.niu.core.service.core.niucloud.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.niucloud.param.SetAuthorizeParam;
import com.niu.core.service.core.niucloud.ICoreNiucloudConfigService;
import com.niu.core.service.core.niucloud.vo.NiucloudConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ICoreNiucloudConfigServiceImpl implements ICoreNiucloudConfigService {

    @Resource
    ICoreConfigService coreConfigService;

    public NiucloudConfigVo getNiucloudConfig() {
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), "NIUCLOUD_CONFIG");
        return JSONUtil.toBean(config, NiucloudConfigVo.class);
    }

    public void setNiucloudConfig(SetAuthorizeParam param) {
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), "NIUCLOUD_CONFIG", JSONUtil.parse(param));
    }

}
