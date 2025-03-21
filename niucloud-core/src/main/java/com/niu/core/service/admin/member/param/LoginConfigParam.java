package com.niu.core.service.admin.member.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 会员参数
*/
@Data
public class LoginConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "is_username参数缺失")
    private Integer isUsername;  // 是否用户名密码登录

    @NotNull(message = "is_mobile参数缺失")
    private Integer isMobile;  // 是否手机验证码登录

    @NotNull(message = "is_auth_register参数缺失")
    private Integer isAuthRegister;  // 是否第三方自动注册

    @NotNull(message = "is_force_access_user_info参数缺失")
    private Integer isForceAccessUserInfo;

    @NotNull(message = "is_bind_mobile参数缺失")
    private Integer isBindMobile;  // 是否强制绑定手机

    @NotNull(message = "agreement_show参数缺失")
    private Integer agreementShow;  // 政策协议是否展示

    @NotNull(message = "bg_url参数缺失")
    private String bgUrl;

    @NotNull(message = "desc参数缺失")
    private String desc;

}
