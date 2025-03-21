package com.niu.core.service.admin.auth.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 登录配置
 */
@Data
public class LoginConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "bg参数缺失")
    private String bg;

    @NotNull(message = "is_captcha参数缺失")
    private Integer isCaptcha;

    @NotNull(message = "is_site_captcha参数缺失")
    private Integer isSiteCaptcha;

    @NotNull(message = "site_bg参数缺失")
    private String siteBg;
}
