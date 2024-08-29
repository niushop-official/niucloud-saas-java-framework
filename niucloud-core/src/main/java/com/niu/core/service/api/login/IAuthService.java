package com.niu.core.service.api.login;

import com.niu.core.entity.member.Member;
import com.niu.core.service.admin.member.vo.LoginConfigVo;
import com.niu.core.service.api.login.param.AccountLoginParam;
import com.niu.core.service.api.login.param.MobileLoginParam;
import com.niu.core.service.api.login.param.ResetPasswordParam;
import com.niu.core.service.api.login.param.SendMobileCodeParam;
import com.niu.core.service.api.login.vo.LoginVo;
import com.niu.core.service.api.login.vo.SendMobileCodeVo;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录服务层
 */
public interface IAuthService {

    /**
     * 登录 检测站点的合法性
     */
    void checkSite();

    /**
     * 校验会员和站点
     *
     */
    void checkSiteAuth();

    /**
     * 校验渠道
     *
     */
    void checkChannel();


}
