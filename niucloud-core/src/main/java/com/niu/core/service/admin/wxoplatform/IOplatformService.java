package com.niu.core.service.admin.wxoplatform;

import com.niu.core.service.admin.wxoplatform.param.AuthorizationParam;

/**
 *
 */
public interface IOplatformService {

    /**
     * 查询微信需要的静态信息
     *
     * @return
     */
    String createPreAuthorizationUrl();

    /**
     * 授权
     *
     * @param param
     */
    void authorization(AuthorizationParam param);

    /**
     * 清理授权
     * @param appid
     */
    void clearAuthorization(String appid);
}
