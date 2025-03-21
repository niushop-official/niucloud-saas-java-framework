package com.niu.core.service.core.wechat.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.enums.sys.ConfigKeyEnum;
import com.niu.core.service.admin.sys.vo.SysCopyRightVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.core.service.core.wechat.ICoreWechatConfigService;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;
import me.chanjar.weixin.open.bean.result.WxOpenAuthorizerInfoResult;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class CoreWechatConfigServiceImpl implements ICoreWechatConfigService {

    @Resource
    ICoreConfigService coreConfigService;

    /**
     * 获取公众号配置
     * @param siteId
     * @return
     */
    @Override
    public WechatConfigVo getWechatConfig(Integer siteId) {
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(siteId, ConfigKeyEnum.WECHAT.getName());
        WechatConfigVo vo = new WechatConfigVo();
        if(ObjectUtil.isNotNull(coreSysConfigVo.getValueJson()))
        {
            vo = JSONUtil.toBean(coreSysConfigVo.getValueJson(), WechatConfigVo.class);
        }
        return vo;
    }

    /**
     * 设置公众号配置
     * @param siteId
     * @param wechatConfigParam
     */
    @Override
    public void setWechatConfig(Integer siteId, WechatConfigParam wechatConfigParam) {
        coreConfigService.setConfig(siteId, ConfigKeyEnum.WECHAT.getName(), JSONUtil.parseObj(wechatConfigParam));
    }

    /**
     * 设置小程序授权信息
     * @param siteId
     * @param wechatAuthorizationInfo
     */
    @Override
    public void setWechatAuthorizationInfo(Integer siteId, WxOpenAuthorizerInfoResult wechatAuthorizationInfo) {
        coreConfigService.setConfig(siteId, ConfigKeyEnum.WECHAT_AUTHORIZATION_INFO.getName(), JSONUtil.parseObj(wechatAuthorizationInfo));
    }

    /**
     *  获取小程序授权信息
     * @param siteId
     * @return
     */
    @Override
    public WxOpenAuthorizerInfoResult getWechatAuthorizationInfo(Integer siteId) {
        JSONObject config =  coreConfigService.getConfigValue(siteId, ConfigKeyEnum.WECHAT_AUTHORIZATION_INFO.getName());
        if (config == null) return null;
        return JSONUtil.toBean(config, WxOpenAuthorizerInfoResult.class);
    }
}
