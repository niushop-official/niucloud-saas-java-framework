package com.niu.core.service.api.login.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SendMobileCodeParam implements ApiBaseParam {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "mobile参数缺失")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    private String type;

    private String captchaKey = "";

    private String captchaCode = "";
}
