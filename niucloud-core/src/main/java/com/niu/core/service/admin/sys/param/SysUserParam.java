package com.niu.core.service.admin.sys.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 后台管理员参数
*/
@Data
public class SysUserParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer uid;

    @NotNull(message = "username参数缺失")
    private String username;

    @NotNull(message = "headImg参数缺失")
    private String headImg;

    @NotNull(message = "password参数缺失")
    private String password;

    @NotNull(message = "realName参数缺失")
    private String realName;

    @NotNull(message = "status参数缺失")
    private Integer status;

    private String mobile;

    private List<SysCreateSiteLimitParam> createSiteLimit;

}
