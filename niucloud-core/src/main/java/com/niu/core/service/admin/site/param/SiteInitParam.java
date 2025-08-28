package com.niu.core.service.admin.site.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SiteInitParam {
    @NotNull(message = "站点不存在")
    private Integer siteId;

    @NotEmpty(message = "captchaCode参数缺失")
    private String captchaCode;

    @NotEmpty(message = "captchaKey参数缺失")
    private String captchaKey;
}
