package com.niu.core.service.api.wechat.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class AuthRegisterParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "openid参数缺失")
    @NotBlank(message = "openid不能为空")
    private String openid;

    private String unionid = "";

    private String mobile = "";

    private String mobileKey = "";

    private String mobileCode = "";

    private Integer pid;

}
