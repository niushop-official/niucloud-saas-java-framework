package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户权限参数
 */
@Data
public class SysUserRoleSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer uid;

    private Integer siteId;

    private String roleIds;

    private Integer isAdmin;

    private Integer status;

}
