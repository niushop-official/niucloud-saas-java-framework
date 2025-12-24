package com.niu.core.service.admin.member.param;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 会员参数
*/
@Data
public class MemberAddParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nickname;

    @NotNull(message = "mobile参数缺失")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @NotNull(message = "member_no参数缺失")
    @NotBlank(message = "会员编码不能为空")
    private String memberNo;

    private String initMemberNo;

    @NotNull(message = "password参数缺失")
    @NotBlank(message = "密码不能为空")
    private String password;

    private String headimg;

    private List<Integer> memberLabel;

    private Integer sex = 0;

    private String birthday;

    private String remark;

    private String idCard;

    private String username;
}
