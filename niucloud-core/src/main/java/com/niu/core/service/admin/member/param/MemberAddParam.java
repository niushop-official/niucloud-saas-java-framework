package com.niu.core.service.admin.member.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员参数
*/
@Data
public class MemberAddParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String memberNo;

    private String initMemberNo;

    private String username;

    @NotNull(message = "mobile参数缺失")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @NotNull(message = "password参数缺失")
    @NotBlank(message = "密码不能为空")
    private String password;

    @NotNull(message = "nickname参数缺失")
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private String headimg;

}
