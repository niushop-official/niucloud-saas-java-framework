package com.niu.core.service.core.weapp.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.enums.sys.ConfigKeyEnum;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.core.service.core.weapp.ICoreWeappConfigService;
import com.niu.core.service.core.weapp.param.WeappConfigParam;
import com.niu.core.service.core.weapp.vo.WeappConfigVo;
import com.niu.core.service.core.wechat.ICoreWechatConfigService;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;
import me.chanjar.weixin.open.bean.result.WxOpenAuthorizerInfoResult;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class CoreWeappConfigServiceImpl implements ICoreWeappConfigService {

    @Resource
    ICoreConfigService coreConfigService;

    @Override
    public WeappConfigVo getWeappConfig(Integer siteId) {

        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(siteId, ConfigKeyEnum.WEAPP.getName());
        WeappConfigVo vo = new WeappConfigVo();
        if(ObjectUtil.isNotNull(coreSysConfigVo.getValueJson()))
        {
            vo = JSONUtil.toBean(coreSysConfigVo.getValueJson(), WeappConfigVo.class);
        }
        return vo;
    }

    @Override
    public void setWeappConfig(Integer siteId, WeappConfigParam weappConfigParam) {
        coreConfigService.setConfig(siteId, ConfigKeyEnum.WEAPP.getName(), JSONUtil.parseObj(weappConfigParam));
    }

    @Override
    public WxOpenAuthorizerInfoResult getWeappAuthorizationInfo(Integer siteId) {
        JSONObject config =  coreConfigService.getConfigValue(siteId, ConfigKeyEnum.WEAPP_AUTHORIZATION_INFO.getName());
        if (config == null) return null;
        return JSONUtil.toBean(config, WxOpenAuthorizerInfoResult.class);
    }

    @Override
    public void setWeappAuthorizationInfo(Integer siteId, WxOpenAuthorizerInfoResult weappAuthorizationInfo) {
        coreConfigService.setConfig(siteId, ConfigKeyEnum.WEAPP_AUTHORIZATION_INFO.getName(), JSONUtil.parseObj(weappAuthorizationInfo));
    }
}
