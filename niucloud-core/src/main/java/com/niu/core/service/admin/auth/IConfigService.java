package com.niu.core.service.admin.auth;

import com.niu.core.service.admin.auth.param.LoginConfigParam;
import com.niu.core.service.admin.auth.vo.LoginConfigVo;

public interface IConfigService {

    /**
     * 获取站点登录配置
     * @return
     */
    LoginConfigVo getLoginConfig();

    /**
     * 更新登录配置信息
     *
     * @param loginConfigParam
     */
    void setLoginConfig(LoginConfigParam loginConfigParam);

}
