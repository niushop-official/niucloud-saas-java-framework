package com.niu.core.service.admin.sys.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class SysLoginConfigParam {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "is_captcha参数缺失")
    private Integer isCaptcha;

    @NotNull(message = "is_site_captcha参数缺失")
    private Integer isSiteCaptcha;

    @NotNull(message = "bg参数缺失")
    private String bg;

    @NotNull(message = "site_bg参数缺失")
    private String siteBg;

}
