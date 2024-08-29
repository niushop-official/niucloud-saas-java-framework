package com.niu.core.service.core.wechat.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.niu.core.service.admin.sys.vo.SysCopyRightVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.core.service.core.wechat.ICoreWechatConfigService;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(siteId, "WECHAT");
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
        coreConfigService.setConfig(siteId, "WECHAT", JSONUtil.parseObj(wechatConfigParam));
    }
}
