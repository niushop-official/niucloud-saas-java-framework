package com.niu.recharge.service.core;

import com.niu.recharge.service.core.param.RechargeSetConfigParam;
import com.niu.recharge.service.core.vo.RechargeConfigVo;

public interface ICoreRechargeConfigService {
    void setRechargeConfig(Integer siteId, RechargeSetConfigParam param);

    RechargeConfigVo getRechargeConfig(Integer siteId);
}
