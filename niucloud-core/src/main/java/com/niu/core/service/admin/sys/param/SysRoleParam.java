package com.niu.core.service.admin.sys.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 角色参数
*/
@Data
public class SysRoleParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer roleId;

    @NotNull(message = "roleName参数缺失")
    private String roleName;

    @NotNull(message = "rules参数缺失")
    private List<String> rules;

    @NotNull(message = "status参数缺失")
    private Integer status;

}
