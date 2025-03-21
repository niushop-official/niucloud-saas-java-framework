package com.niu.core.common.component.pay.impl;

import cn.hutool.json.JSONObject;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.pay.AbstractPayProvider;
import com.niu.core.common.component.pay.IPayProvider;
import com.niu.core.common.component.pay.annotation.PayProvider;
import com.niu.core.common.component.pay.model.*;
import com.niu.core.common.component.pay.model.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@PayProvider(name = "wechat", describe = "微信支付", source = "core",
        icon = "static/resource/icon/pay_icon/wechatpay.png", settingComponent = "")
public class WechatPayProviderImpl extends AbstractPayProvider implements IPayProvider {

    @Override
    public PayModelResult pay(JSONObject initObject, PayModel payModel) {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setMchId(initObject.getStr("mch_id", ""));
        payConfig.setApiV3Key(initObject.getStr("mch_secret_key", ""));
        payConfig.setPrivateCertPath(WebAppEnvs.get().webRootDownResource + initObject.getStr("mch_public_cert_path", ""));
        payConfig.setPrivateKeyPath(WebAppEnvs.get().webRootDownResource + initObject.getStr("mch_secret_cert", ""));
        return new PayModelResult();
    }

    @Override
    public AsyncNotifyModelResult asyncNotify(JSONObject initObject, HttpServletRequest request, HttpServletResponse response, AsyncNotifyModel asyncNotifyModel) {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setMchId(initObject.getStr("mch_id", ""));
        payConfig.setApiV3Key(initObject.getStr("mch_secret_key", ""));
        payConfig.setPrivateCertPath(WebAppEnvs.get().webRootDownResource + initObject.getStr("mch_public_cert_path", ""));
        payConfig.setPrivateKeyPath(WebAppEnvs.get().webRootDownResource + initObject.getStr("mch_secret_cert", ""));

        return null;
    }

    @Override
    public PayInfoModelResult info(JSONObject initObject, PayInfoModel payInfoModel) {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setMchId(initObject.getStr("mch_id", ""));
        payConfig.setApiV3Key(initObject.getStr("mch_secret_key", ""));
        payConfig.setPrivateCertPath(WebAppEnvs.get().webRootDownResource + initObject.getStr("mch_public_cert_path", ""));
        payConfig.setPrivateKeyPath(WebAppEnvs.get().webRootDownResource + initObject.getStr("mch_secret_cert", ""));

        return null;
    }

}
