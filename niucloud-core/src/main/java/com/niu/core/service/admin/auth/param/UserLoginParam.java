package com.niu.core.service.admin.auth.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UserLoginParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String action;

    @NotNull(message = "账号参数缺失")
    private String username;

    @NotNull(message = "密码参数缺失")
    private String password;

    @NotNull(message = "登录类型参数缺失")
    private String appType;

    private String captchaKey;//验证码

    private String captchaCode;//验证码参数
}
