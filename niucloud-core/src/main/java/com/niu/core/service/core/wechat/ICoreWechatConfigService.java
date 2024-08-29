package com.niu.core.service.core.wechat;

import com.niu.core.service.core.wechat.param.WechatConfigParam;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;

/**
 * 微信配置core服务层
 */
public interface ICoreWechatConfigService {

    WechatConfigVo getWechatConfig(Integer siteId);

    void setWechatConfig(Integer siteId, WechatConfigParam wechatConfigParam);

}
