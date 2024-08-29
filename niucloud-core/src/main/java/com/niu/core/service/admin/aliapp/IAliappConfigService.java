package com.niu.core.service.admin.aliapp;

import com.niu.core.service.core.aliapp.param.AliappConfigParam;
import com.niu.core.service.core.aliapp.vo.AliappConfigVo;

import java.util.Map;

/**
 * 支付宝小程序配置服务层
 */
public interface IAliappConfigService {
    AliappConfigVo getAliappConfig();
    void setAliappConfig(AliappConfigParam aliappConfigParam);
    Map<String, String> getStatic();

}