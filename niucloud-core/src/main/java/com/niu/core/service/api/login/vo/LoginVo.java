package com.niu.core.service.api.login.vo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 登录视图
 */
@Data
public class LoginVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String token;

    private Long expiresTime;

    private String mobile;

    private String avatar = "";

    private String nickname = "";

    private String openid = "";

    private String unionid = "";
}
