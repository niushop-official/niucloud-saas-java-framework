package com.niu.core.service.core.captcha.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 滑动验证码 验证类
 *
 */
@Data
public class CoreCaptchaValiDateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String captchaType;

    private String captchaCode;

    private String captchaKey;
}
