package com.niu.core.enums.sys;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConfigKeyEnum {
    NIUCLOUD_CONFIG("NIUCLOUD_CONFIG"),//牛云配置
    WECHAT("WECHAT"),//微信公众号
    WEAPP("weapp"),//微信小程序
    WECHAT_PAY("wechat_pay"),//微信支付
    ALIPAY("alipay"),//支付宝支付
    OFFLINE_PAY("offline_pay"),//线下支付
    UPLOAD("upload"),//上传配置
    DIY_BOTTOM("diy_bottom"),//底部导航配置
    MEMBER_CASH_OUT("member_cash_out"),//会员提现
    ADMIN_LOGIN("admin_login"),//管理端登录注册设置
    ALIAPP("aliapp"),//支付宝小程序
    H5("h5"),//h5
    WXOPLATFORM("WXOPLATFORM"), // 微信开放平台
    WEAPP_AUTHORIZATION_INFO("weapp_authorization_info"),
    WECHAT_AUTHORIZATION_INFO("wechat_authorization_info"),
    WECHAT_TRANSFER_SCENE_CONFIG("WECHAT_TRANSFER_SCENE_CONFIG");
    
    private final String name;
}
