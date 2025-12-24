package com.niu.core.service.admin.site.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 编辑站点参数
 */
@Data
public class SiteEditParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteName参数缺失")
    private String siteName;

    @NotNull(message = "groupId参数缺失")
    private Integer groupId;

    @NotNull(message = "expire_time参数缺失")
    private String expireTime;

    @NotNull(message = "siteDomain参数缺失")
    private String siteDomain;

    @NotNull(message = "uid 参数缺失")
    private Integer uid;

    @NotNull(message = "username参数缺失")
    private String username;

    @NotNull(message = "real_name参数缺失")
    private String realName;

    @NotNull(message = "password参数缺失")
    private String password;
}
