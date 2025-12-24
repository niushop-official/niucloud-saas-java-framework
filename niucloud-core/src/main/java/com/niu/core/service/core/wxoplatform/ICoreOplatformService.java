package com.niu.core.service.core.wxoplatform;

import me.chanjar.weixin.open.bean.result.WxOpenVersioninfoResult;

public interface ICoreOplatformService {

    /**
     * 根据appid获取站点id
     * @param appid
     * @return
     */
    Integer getSiteIdByAuthorizerAppid(String appid);

    WxOpenVersioninfoResult weappVersion(Integer siteId);
}
