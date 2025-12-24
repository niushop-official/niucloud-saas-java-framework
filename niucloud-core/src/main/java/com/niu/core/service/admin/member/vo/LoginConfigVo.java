package com.niu.core.service.admin.member.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * Member视图
 */
@Data
public class LoginConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer isUsername = 1;  // 是否用户名密码登录
    private Integer isMobile = 0;  // 是否手机验证码登录
    private Integer isAuthRegister = 1;  // 是否第三方自动注册
    private Integer isForceAccessUserInfo = 0; // 是否强制获取用户信息
    private Integer isBindMobile = 0;  // 是否强制绑定手机
    private Integer agreementShow = 0;  // 政策协议是否展示
    private String bgUrl = "";
    private String desc = "精选好物，购物优惠的省钱平台";
    private String wechatError;
}
