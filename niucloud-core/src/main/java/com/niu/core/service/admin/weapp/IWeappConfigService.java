package com.niu.core.service.admin.weapp;

import com.niu.core.service.admin.weapp.vo.WeappStaticInfoVo;
import com.niu.core.service.admin.wechat.vo.WechatStaticInfoVo;
import com.niu.core.service.core.weapp.param.WeappConfigParam;
import com.niu.core.service.core.weapp.vo.WeappConfigVo;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;

/**
 * 微信配置
 */
public interface IWeappConfigService {

    WeappConfigVo getWeappConfig();

    void setWeappConfig(WeappConfigParam weappConfigParam);
}
