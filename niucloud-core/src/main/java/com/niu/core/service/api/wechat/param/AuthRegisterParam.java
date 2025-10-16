package com.niu.core.service.api.wechat.param;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class AuthRegisterParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private String openid;

    private String unionid = "";

    private String mobile = "";

    private String mobileKey = "";

    private String mobileCode = "";

    private String nickname = "";

    private String headimg = "";

    private Integer pid;

    private String registerType;

}
