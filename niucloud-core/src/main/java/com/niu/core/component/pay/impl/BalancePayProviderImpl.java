package com.niu.core.component.pay.impl;

import cn.hutool.json.JSONObject;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.niu.core.component.context.WebAppEnvs;
import com.niu.core.component.pay.AbstractPayProvider;
import com.niu.core.component.pay.IPayProvider;
import com.niu.core.component.pay.annotation.PayProvider;
import com.niu.core.component.pay.model.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@PayProvider(name = "balance", describe = "余额支付", icon = "",
        settingComponent = "static/resource/icon/pay_icon/balancepay.png", source = "core")
public class BalancePayProviderImpl extends AbstractPayProvider implements IPayProvider {

    @Override
    public PayModelResult pay(JSONObject initObject, PayModel payModel) {
        Integer memberId = initObject.getInt("memberId");
        return new PayModelResult();
    }

    @Override
    public AsyncNotifyModelResult asyncNotify(JSONObject initObject, HttpServletRequest request, HttpServletResponse response, AsyncNotifyModel asyncNotifyModel) {

        return null;
    }

    @Override
    public PayInfoModelResult info(JSONObject initObject, PayInfoModel payInfoModel) {

        return null;
    }

}
