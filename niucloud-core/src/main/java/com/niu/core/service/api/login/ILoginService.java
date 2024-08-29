package com.niu.core.service.api.login;

import com.niu.core.entity.member.Member;
import com.niu.core.service.admin.member.vo.LoginConfigVo;
import com.niu.core.service.api.login.param.*;
import com.niu.core.service.api.login.vo.LoginVo;
import com.niu.core.service.api.login.vo.SendMobileCodeVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 登录接口
 */
public interface ILoginService {

    LoginVo account(AccountLoginParam param);

    LoginVo mobile(MobileLoginParam param);

    LoginVo login(Member member);

    void resetPassword(ResetPasswordParam param);

    LoginConfigVo getLoginConfig();

    SendMobileCodeVo sendMobileCode(SendMobileCodeParam param);

    void logout();
}
