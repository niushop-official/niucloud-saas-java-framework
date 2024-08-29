package com.niu.core.service.admin.sys.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * SysUserRoleVo
 */
@Data
public class SysUserRoleInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**id */
    private Integer id;

    /**用户id */
    private Integer uid;

    /**站点id */
    private Integer siteId;

    /**角色id */
    private String roleIds;

    /**是否是超级管理员 */
    private Integer isAdmin;

    /**状态 */
    private Integer status;

}
