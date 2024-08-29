package com.niu.core.service.api.login.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class MobileLoginParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "mobile参数缺失")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    private String mobileKey = "";

    private String mobileCode = "";
}
