package com.niu.core.service.admin.auth;

import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.auth.param.UserLoginParam;
import com.niu.core.service.admin.auth.vo.LoginResultVo;

/**
 * 用户登录服务层
 */
public interface ILoginService {
    /**
     * 用户登录
     * @param userLoginParam
     * @return
     */
    LoginResultVo login(UserLoginParam userLoginParam);

    /**
     * 用戶登出
     *
     */
    void logout();

    /**
     * 清理token
     * @param uid
     * @param appType
     * @param token
     */
    void clearToken(Integer uid, String appType, String token);
}
