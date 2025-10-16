package com.niu.core.service.core.channel.vo;

import lombok.Data;

/**
 * APP配置信息VO
 */
@Data
public class AppConfigVo {

    /**
     * 微信AppID
     */
    private String wechatAppId;

    /**
     * 微信AppSecret
     */
    private String wechatAppSecret;

    /**
     * Android App Key
     */
    private String androidAppKey;

    /**
     * 应用ID
     */
    private String applicationId;

    /**
     * UniApp ID
     */
    private String uniAppId;

    /**
     * App名称
     */
    private String appName;
}