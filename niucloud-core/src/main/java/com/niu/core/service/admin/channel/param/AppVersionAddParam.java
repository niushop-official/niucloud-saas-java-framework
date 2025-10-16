package com.niu.core.service.admin.channel.param;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 添加版本参数
 */
@Data
public class AppVersionAddParam {

    private Integer siteId;

    /**
     * 版本代码
     */
    @NotEmpty(message = "版本代码不能为空")
    private String versionCode;

    /**
     * 版本号
     */
    @NotEmpty(message = "版本号不能为空")
    private String versionName;

    private String versionDesc;

    /**
     * 平台类型 (1:Android, 2:iOS)
     */
    @NotNull(message = "平台类型不能为空")
    private String platform;

    /**
     * 强制更新 (0:否, 1:是)
     */
    private Integer isForceUpdate = 0;

    private String packagePath;

    private String packageType;

    private String upgradeType;

    private Build build;

    private Cert cert;

    @Data
    public static class Build {
        private String icon;
    }

    @Data
    public static class Cert {
        private String type;

        private String file;

        @JsonProperty("key_alias")
        private String keyAlias;

        @JsonProperty("key_password")
        private String keyPassword;

        @JsonProperty("store_password")
        private String storePassword;
    }
}