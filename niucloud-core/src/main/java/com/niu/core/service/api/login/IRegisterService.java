package com.niu.core.service.api.login;

import com.niu.core.entity.member.Member;
import com.niu.core.service.api.login.param.AccountRegisterParam;
import com.niu.core.service.api.login.param.MobileRegisterParam;
import com.niu.core.service.api.login.vo.LoginVo;

/**
 * 注册接口
 */
public interface IRegisterService {

    void checkLoginConfig(String type);

    LoginVo account(AccountRegisterParam param);

    LoginVo mobile(MobileRegisterParam param);

    LoginVo register(Member member);

    void checkMobileCode(String mobile, String mobileKey, String mobileCode);
}
