package com.niu.core.service.api.weapp;

import com.niu.core.service.api.login.vo.LoginVo;
import com.niu.core.service.api.wechat.param.AuthRegisterParam;
import com.niu.core.service.api.wechat.param.WechatAuthDataLoginParam;
import com.niu.core.service.api.wechat.param.WechatAuthParam;
import com.niu.core.service.api.wechat.param.WechatSyncParam;
import com.niu.core.service.api.wechat.vo.WechatCodeUrlVo;
import com.niu.core.service.api.wechat.vo.WechatScanLoginVo;
import com.niu.core.service.core.weapp.vo.IsTradeManagedVo;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface IWeappService {

    /**
     * 授权登录
     * @param param
     * @return
     */
    LoginVo loginByCode(WechatAuthParam param);

    /**
     * 授权信息注册账号
     * @param param
     * @return
     */
    LoginVo register(AuthRegisterParam param);

    /**
     * 小程序更新用户openid
     * @param param
     */
    void updateOpenid(WechatAuthParam param);

    List<String> subscribeMessage(String keys);

    /**
     * 查询小程序是否已开通发货信息管理服务
     * @return
     */
    IsTradeManagedVo getIsTradeManaged();

}
