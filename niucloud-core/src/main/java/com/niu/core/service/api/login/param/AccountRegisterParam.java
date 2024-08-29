package com.niu.core.service.api.login.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class AccountRegisterParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "username参数缺失")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotNull(message = "password参数缺失")
    @NotBlank(message = "密码不能为空")
    private String password;

    private String mobile = "";

    private String mobileKey = "";

    private String mobileCode = "";
}
