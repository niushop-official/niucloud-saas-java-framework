package com.niu.core.service.admin.site.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 用户参数
*/
@Data
public class SiteUserParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer uid;

    @NotNull(message = "账号参数缺失")
    private String username; // 用户账号

    private String headImg; //头像

    @NotNull(message = "密码参数缺失")
    private String password; // 用户密码

    @NotNull(message = "姓名参数缺失")
    private String realName; // 实际姓名

    @NotNull(message = "用户状态参数缺失")
    private Integer status; // 用户状态

    @NotNull(message = "角色参数缺失")
    private List<String> roleIds;//角色id

    private Integer isAdmin;//是否是管理员
}
