package com.niu.core.common.component.pay.impl;

import cn.hutool.json.JSONObject;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.pay.AbstractPayProvider;
import com.niu.core.common.component.pay.IPayProvider;
import com.niu.core.common.component.pay.annotation.PayProvider;
import com.niu.core.common.component.pay.model.*;
import com.niu.core.common.component.pay.model.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@PayProvider(name = "alipay", source = "core", describe = "支付宝支付",
        icon = "static/resource/icon/pay_icon/alipay.png", settingComponent = "")
public class AlipayPayProviderImpl extends AbstractPayProvider implements IPayProvider {

    @Override
    public PayModelResult pay(JSONObject initObject, PayModel payModel) {
        //
        Config option = new Config();
        option.protocol = "https";
        option.gatewayHost = "openapi.alipay.com";
        option.signType = "RSA2";
        option.appId = initObject.getStr("app_id", "");
        option.merchantPrivateKey = initObject.getStr("app_secret_cert", "");
        option.merchantCertPath = WebAppEnvs.get().webRootDownResource + initObject.getStr("app_public_cert_path", "");
        option.alipayCertPath = WebAppEnvs.get().webRootDownResource + initObject.getStr("alipay_public_cert_path", "");
        option.alipayRootCertPath = WebAppEnvs.get().webRootDownResource + initObject.getStr("alipay_root_cert_path", "");
        Factory.setOptions(option);
        //
        return null;
    }

    @Override
    public AsyncNotifyModelResult asyncNotify(JSONObject initObject, HttpServletRequest request, HttpServletResponse response, AsyncNotifyModel asyncNotifyModel) {
        //
        Config option = new Config();
        option.protocol = "https";
        option.gatewayHost = "openapi.alipay.com";
        option.signType = "RSA2";
        option.appId = initObject.getStr("app_id", "");
        option.merchantPrivateKey = initObject.getStr("app_secret_cert", "");
        option.merchantCertPath = WebAppEnvs.get().webRootDownResource + initObject.getStr("app_public_cert_path", "");
        option.alipayCertPath = WebAppEnvs.get().webRootDownResource + initObject.getStr("alipay_public_cert_path", "");
        option.alipayRootCertPath = WebAppEnvs.get().webRootDownResource + initObject.getStr("alipay_root_cert_path", "");
        Factory.setOptions(option);
        //

        return null;
    }

    @Override
    public PayInfoModelResult info(JSONObject initObject, PayInfoModel payInfoModel) {
        //
        Config option = new Config();
        option.protocol = "https";
        option.gatewayHost = "openapi.alipay.com";
        option.signType = "RSA2";
        option.appId = initObject.getStr("app_id", "");
        option.merchantPrivateKey = initObject.getStr("app_secret_cert", "");
        option.merchantCertPath = WebAppEnvs.get().webRootDownResource + initObject.getStr("app_public_cert_path", "");
        option.alipayCertPath = WebAppEnvs.get().webRootDownResource + initObject.getStr("alipay_public_cert_path", "");
        option.alipayRootCertPath = WebAppEnvs.get().webRootDownResource + initObject.getStr("alipay_root_cert_path", "");
        Factory.setOptions(option);
        //
        return null;
    }


}
