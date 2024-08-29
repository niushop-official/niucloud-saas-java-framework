package com.niu.core.service.admin.sys.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * SysUserRoleVo
 */
@Data
public class SysUserRoleListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer uid;  // 系统用户ID
    private String username;  // 用户账号
    private String headImg;  //
    private String password;  // 用户密码
    private String realName;  // 实际姓名
    private String lastIp;  // 最后一次登录ip
    private Integer lastTime;  // 最后一次登录时间
    private Integer loginCount;  // 登录次数
    private Integer status;  //
    private Integer isDel;  //
    private Integer id;  //
    private Integer siteId;  // 站点id
    private String roleIds;  // 角色id
    private String createTime; // 添加时间
    private Integer isAdmin;  // 是否是超级管理员

}
