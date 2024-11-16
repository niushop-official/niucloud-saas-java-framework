package com.niu.core.service.admin.site.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
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

}
