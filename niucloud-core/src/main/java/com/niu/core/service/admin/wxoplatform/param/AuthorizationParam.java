package com.niu.core.service.admin.wxoplatform.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信开放平台更新资料
*/
@Data
public class AuthorizationParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "auth_code参数缺失")
    private String authCode;

    private String expiresIn;

}
