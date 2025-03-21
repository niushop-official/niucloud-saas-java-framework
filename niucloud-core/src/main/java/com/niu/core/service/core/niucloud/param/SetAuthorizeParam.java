package com.niu.core.service.core.niucloud.param;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
public class SetAuthorizeParam {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "缺少参数auth_code")
    @NotEmpty(message = "授权码不能为空")
    private String authCode;

    @NotNull(message = "缺少参数auth_secret")
    @NotEmpty(message = "授权秘钥不能为空")
    private String authSecret;

}
