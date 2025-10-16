package com.niu.core.service.admin.channel.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 编辑版本参数
 */
@Data
public class AppVersionEditParam {

    /**
     * 版本ID
     */
    @NotNull(message = "版本ID不能为空")
    private Integer id;

    /**
     * 版本号
     */
    @NotEmpty(message = "版本号不能为空")
    private String version;

    /**
     * 版本代码
     */
    @NotEmpty(message = "版本代码不能为空")
    private String versionCode;

    /**
     * 版本描述
     */
    private String remark;

    /**
     * 下载地址
     */
    private String downloadUrl;

    /**
     * 强制更新 (0:否, 1:是)
     */
    private Integer forceUpdate = 0;

    /**
     * 图标地址
     */
    private String iconUrl;
}