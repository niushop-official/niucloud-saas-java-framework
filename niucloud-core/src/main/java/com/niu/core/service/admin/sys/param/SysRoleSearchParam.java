package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色参数
 */
@Data
public class SysRoleSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String roleName;

    private String rules;

    private Integer status;

}
