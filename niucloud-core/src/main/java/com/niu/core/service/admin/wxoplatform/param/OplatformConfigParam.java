package com.niu.core.service.admin.wxoplatform.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 微信开放平台更新资料
*/
@Data
public class OplatformConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "aes_key参数缺失")
    private String aesKey;

    @NotNull(message = "app_id参数缺失")
    private String appId;

    @NotNull(message = "app_secret参数缺失")
    private String appSecret;

    @NotNull(message = "develop_app_id参数缺失")
    private String developAppId;

    @NotNull(message = "develop_upload_private_key参数缺失")
    private String developUploadPrivateKey;

    @NotNull(message = "token参数缺失")
    private String token;

}
