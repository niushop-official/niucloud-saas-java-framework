package com.niu.core.service.admin.niucloud.vo;

import lombok.Data;

@Data
public class ModuleListVo {
    private static final long serialVersionUID = 1L;

    @Data
    public class App {
        private Integer siteId;
        private String appName;
        private Integer appId;
        private String appKey;
        private String appType;
        private String appLogo;
        private String appDesc;
        private Integer isIndependent;
        private String[] supportChannel;
        private String[] windowLogo;
        private String lastVersion;
    }

    private String version;

    private String siteName;

    private App app;



}
