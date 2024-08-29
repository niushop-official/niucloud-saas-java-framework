package com.niu.core.service.core.captcha.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.io.Serializable;

/**
 * 验证码返回信息
 *
 */
@Data
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class CoreCaptchaInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 验证码图片 */
    private String jigsawImageBase64;
    /** 验证码图 */
    private String originalImageBase64;
    /** key */
    private String secretKey;
    /** token */
    private String token;
}
