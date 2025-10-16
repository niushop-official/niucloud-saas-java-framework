package com.niu.core.service.core.channel.param;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 设置APP配置参数
 */
@Data
public class SetAppParam {

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