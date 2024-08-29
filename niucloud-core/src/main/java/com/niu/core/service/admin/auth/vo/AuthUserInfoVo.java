package com.niu.core.service.admin.auth.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AuthUserInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer isAdmin;
    private Integer uid;  // 系统用户ID
    private String username;  // 用户账号
    private String headImg;  // 用户头像
    private String password;  // 用户密码
    private String realName;  // 实际姓名
    private String lastIp;  // 最后一次登录ip
    private Integer lastTime;  // 最后一次登录时间
    private Integer loginCount;  // 登录次数
    private Integer status;  //
    private Integer isDel;  //
    private Long createTime; //创建时间
    private List<String> roleIds;//角色id
    private Integer siteId;
    private String statusName;

}
