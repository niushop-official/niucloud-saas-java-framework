package com.niu.recharge.service.api;

import com.niu.recharge.service.api.vo.RechargeListVo;
import com.niu.recharge.service.core.vo.RechargeConfigVo;

import java.util.List;

public interface IRechargeService {
    RechargeConfigVo getRechargeConfig();

    List<RechargeListVo> getRechargeLists();
}
