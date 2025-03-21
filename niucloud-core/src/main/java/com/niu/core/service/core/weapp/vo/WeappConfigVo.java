package com.niu.core.service.core.weapp.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信配置视图
 */

@Data
public class WeappConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String weappName = ""; // 小程序名称
    private String weappOriginal = ""; // 原始ID
    private String appId = ""; // APPID
    private String appSecret = ""; // APPSECRET
    private String qrCode = ""; // 公众号二维码
    private String token = ""; // 公众号token
    private String encodingAesKey = ""; // 加密密钥
    private String encryptionType = "not_encrypt"; // 加密方式
    private String uploadPrivateKey = "";
    private Integer isAuthorization = 0; // 权限设置

    private String serveUrl;
    private String requestUrl;
    private String socketUrl;
    private String uploadUrl;
    private String downloadUrl;
    private String uploadIp;

    private Domain domain = new Domain();

    @Data
    public static class Domain {
        private String requestdomain = "";
        private String wsrequestdomain = "";
        private String uploaddomain = "";
        private String downloaddomain = "";
        private String tcpdomain = "";
        private String udpdomain = "";
    }
}