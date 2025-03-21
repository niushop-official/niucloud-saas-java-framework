package com.niu.core.service.api.wechat;

import com.niu.core.service.api.login.vo.LoginVo;
import com.niu.core.service.api.wechat.param.WechatAuthDataLoginParam;
import com.niu.core.service.api.wechat.param.WechatAuthParam;
import com.niu.core.service.api.wechat.param.AuthRegisterParam;
import com.niu.core.service.api.wechat.param.WechatSyncParam;
import com.niu.core.service.api.wechat.vo.WechatCodeUrlVo;
import com.niu.core.service.api.wechat.vo.WechatScanLoginVo;
import com.niu.core.service.api.wechat.vo.WechatUserInfoVo;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import java.util.Map;

public interface IWechatService {

    /**
     * 获取跳转获取code
     * @return
     */
    WechatCodeUrlVo getCodeUrl(String url, String scopes);

    /**
     * 授权登录
     * @param param
     * @return
     */
    LoginVo loginByCode(WechatAuthParam param);

    /**
     * 公众号通过授权信息登录
     * @param param
     * @return
     */
    LoginVo wechatLogin(WechatAuthDataLoginParam param);

    /**
     * 授权信息注册账号
     * @param param
     * @return
     */
    LoginVo register(AuthRegisterParam param);

    /**
     * 公众号通过code同步授权
     * @param param
     * @return
     */
    void sync(WechatSyncParam param);

    /**
     * 获取网页授权信息
     * @param param
     * @return
     */
    WechatUserInfoVo getWechatUser(WechatAuthParam param);

    /**
     * 获取jssdk config
     * @param url
     * @return
     */
    Map<String, Object> getJssdkConfig(String url);

    /**
     * 扫码登录
     * @return
     */
    WechatScanLoginVo scanLogin();

    /**
     * 公众号更新用户openid
     * @param param
     */
    void updateOpenid(WechatAuthParam param);
}
