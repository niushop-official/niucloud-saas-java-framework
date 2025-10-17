package com.niu.core.service.api.channel.vo;

import lombok.Data;

/**
 * APP配置信息VO
 */
@Data
public class ApiAppConfigVo {

    /**
     * 微信AppID
     */
    private String wechatAppId;

    /**
     * UniApp ID
     */
    private String uniAppId;

    /**
     * App名称
     */
    private String appName;

    private String weappOriginal;
}