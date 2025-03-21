package com.niu.core.service.admin.sys.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户权限参数
*/
@Data
public class SysUserRoleParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "uid参数缺失")
    private Integer uid;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "roleIds参数缺失")
    private String roleIds;

    @NotNull(message = "isAdmin参数缺失")
    private Integer isAdmin;

    @NotNull(message = "status参数缺失")
    private Integer status;

}
