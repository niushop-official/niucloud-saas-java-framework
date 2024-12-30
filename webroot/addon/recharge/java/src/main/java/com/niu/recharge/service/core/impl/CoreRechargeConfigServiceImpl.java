package com.niu.recharge.service.core.impl;

import cn.hutool.json.JSONUtil;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.recharge.service.core.ICoreRechargeConfigService;
import com.niu.recharge.service.core.model.RechargeConfigModel;
import com.niu.recharge.service.core.param.RechargeSetConfigParam;
import com.niu.recharge.service.core.vo.RechargeConfigVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoreRechargeConfigServiceImpl implements ICoreRechargeConfigService {
    private static final String KEY = "RECHARGE_CONFIG";

    private final ICoreConfigService coreConfigService;

    @Override
    public void setRechargeConfig(Integer siteId, RechargeSetConfigParam param) {
        RechargeConfigModel model = new RechargeConfigModel();
        BeanUtils.copyProperties(param, model);
        coreConfigService.setConfig(siteId, KEY, JSONUtil.parseObj(model));
    }

    @Override
    public RechargeConfigVo getRechargeConfig(Integer siteId) {
        CoreSysConfigVo config = coreConfigService.getConfig(siteId, KEY);
        
        RechargeConfigVo bean = JSONUtil.toBean(config.getValue(), RechargeConfigVo.class);
        bean.setIsUse(Optional.ofNullable(bean.getIsUse()).orElse(1));
        bean.setRechargeExplain(Optional.ofNullable(bean.getRechargeExplain()).orElse("充值后帐户的金额不能提现，可用于商城消费使用账户充值出现问题可联系商城客服"));
        bean.setMinPrice(Optional.ofNullable(bean.getMinPrice()).orElse(new BigDecimal("0.01")));
        bean.setCloseLength(Optional.ofNullable(bean.getCloseLength()).orElse(10));
        return bean;
    }
}
