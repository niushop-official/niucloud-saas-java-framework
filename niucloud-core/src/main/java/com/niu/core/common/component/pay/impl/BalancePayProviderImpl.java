package com.niu.core.common.component.pay.impl;

import cn.hutool.json.JSONObject;
import com.niu.core.common.component.pay.AbstractPayProvider;
import com.niu.core.common.component.pay.IPayProvider;
import com.niu.core.common.component.pay.annotation.PayProvider;
import com.niu.core.common.component.pay.model.*;
import com.niu.core.common.component.pay.model.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
