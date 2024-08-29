package com.niu.core.service.core.weapp.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.core.service.core.weapp.ICoreWeappConfigService;
import com.niu.core.service.core.weapp.param.WeappConfigParam;
import com.niu.core.service.core.weapp.vo.WeappConfigVo;
import com.niu.core.service.core.wechat.ICoreWechatConfigService;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CoreWeappConfigServiceImpl implements ICoreWeappConfigService {

    @Resource
    ICoreConfigService coreConfigService;

    @Override
    public WeappConfigVo getWeappConfig(Integer siteId) {

        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(siteId, "weapp");
        WeappConfigVo vo = new WeappConfigVo();
        if(ObjectUtil.isNotNull(coreSysConfigVo.getValueJson()))
        {
            vo = JSONUtil.toBean(coreSysConfigVo.getValueJson(), WeappConfigVo.class);
        }
        return vo;
    }

    @Override
    public void setWeappConfig(Integer siteId, WeappConfigParam weappConfigParam) {
        coreConfigService.setConfig(siteId, "weapp", JSONUtil.parseObj(weappConfigParam));
    }

    @Override
    public JSONObject getWeappAuthorizationInfo(Integer siteId) {
        return coreConfigService.getConfigValue(siteId, "weapp_authorization_info");
    }

    @Override
    public void setWeappAuthorizationInfo(Integer siteId, JSONObject weappAuthorizationInfo) {
        coreConfigService.setConfig(siteId, "weapp_authorization_info", weappAuthorizationInfo);
    }
}
