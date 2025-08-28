package com.niu.core.service.admin.niucloud.vo;

import lombok.Data;

@Data
public class AppVersionListVo {

    private App app;

    private String releaseTime;

    private String upgradeLog;

    private String versionNo;

    private String updateTime;

    private Integer versionId;

    private Integer appId;

    @Data
    public static class App {
        private Integer appId;

        private String appName;

        private String appKey;

        private String productKey;
    }

}
