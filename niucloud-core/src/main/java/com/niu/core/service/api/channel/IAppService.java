package com.niu.core.service.api.channel;

import com.niu.core.service.api.channel.param.GetNewVersionParam;
import com.niu.core.service.api.channel.vo.ApiAppConfigVo;
import com.niu.core.service.api.channel.vo.NewVersionVo;
import com.niu.core.service.api.login.vo.LoginVo;
import com.niu.core.service.api.wechat.param.AuthRegisterParam;
import com.niu.core.service.api.wechat.param.WechatAuthParam;

public interface IAppService {

    /**
     * 微信登录
     * @param param
     * @return
     */
    LoginVo wechatLogin(WechatAuthParam param);

    /**
     * 获取新的版本
     * @param param
     * @return
     */
    NewVersionVo getNewVersion(GetNewVersionParam param);

    /**
     * app端第三方注册
     * @param param
     * @return
     */
    LoginVo register(AuthRegisterParam param);

    ApiAppConfigVo getAppConfig();
}
