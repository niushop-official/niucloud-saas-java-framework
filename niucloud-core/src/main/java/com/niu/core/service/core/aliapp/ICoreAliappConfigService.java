package com.niu.core.service.core.aliapp;

import com.niu.core.service.core.aliapp.param.AliappConfigParam;
import com.niu.core.service.core.aliapp.vo.AliappConfigVo;

/**
 * 支付宝小程序配置服务层
 */
public interface ICoreAliappConfigService {
    AliappConfigVo getAliappConfig(Integer siteId);
    void setAliappConfig(Integer siteId, AliappConfigParam aliappConfigParam);

}