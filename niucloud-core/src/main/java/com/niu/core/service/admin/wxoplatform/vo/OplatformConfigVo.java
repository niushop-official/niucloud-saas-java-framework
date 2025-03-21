package com.niu.core.service.admin.wxoplatform.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信开放平台配置
*/
@Data
public class OplatformConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String aesKey;

    private String appId;

    private String appSecret;

    private String developAppId;

    private String developUploadPrivateKey;

    private String token;

}
