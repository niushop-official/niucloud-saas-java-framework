package com.niu.core.service.core.wxoplatform;

public interface ICoreOplatformService {

    /**
     * 根据appid获取站点id
     * @param appid
     * @return
     */
    Integer getSiteIdByAuthorizerAppid(String appid);
}
