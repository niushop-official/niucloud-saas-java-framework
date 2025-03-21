package com.niu.core.service.admin.sys.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 后台管理员添加参数
*/
@Data
public class SysUserAddParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer uid;

    /**用户名 */
    @NotNull(message = "username参数缺失")
    private String username;

    /**密码 */
    @NotNull(message = "password参数缺失")
    private String password;

    /**套餐权限 */
    private List<SysCreateSiteLimitParam> createSiteLimit;

    /**套餐id */
    private List<Integer> groupIds;

}
