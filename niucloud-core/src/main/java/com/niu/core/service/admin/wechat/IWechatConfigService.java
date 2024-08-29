package com.niu.core.service.admin.wechat;

import com.niu.core.service.admin.wechat.vo.WechatStaticInfoVo;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;

/**
 * 微信配置
 */
public interface IWechatConfigService {

    WechatConfigVo getWechatConfig();

    void setWechatConfig(WechatConfigParam wechatConfigParam);

    WechatStaticInfoVo staticInfo();
}
