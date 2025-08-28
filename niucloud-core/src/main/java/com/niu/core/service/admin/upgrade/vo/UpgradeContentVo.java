package com.niu.core.service.admin.upgrade.vo;

import com.niu.core.entity.sys.SysBackupRecords;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class UpgradeContentVo {

    private List<Content> content = new LinkedList<>();

    private List<String> upgradeApps = new LinkedList<>();

    private SysBackupRecords lastBackup = null;

    @Data
    public static class Content {
        private App app;
        private String version;
        private String lastVersion;
        private List<VersionItem> versionList;
        private String upgradeVersion;
        private String expireTime;
        private Integer isExpire;
    }

    @Data
    public static class App {
        private Integer appId;
        private String appKey;
        private String appLogo;
        private String appName;
        private String appDesc;
        private String lastVersion;
        private String lastUpdateTime;
    }

    @Data
    public static class NiucloudVersion {
        private String statusName;
        private Integer versionId;
        private Integer siteId;
        private Integer appId;
        private String versionNo;
        private String upgradeLog;
        private String versionPackage;
        private Integer status;
        private String refuseReason;
        private String createTime;
        private String updateTime;
        private String releaseTime;
        private Integer niucloudVersionId;
        private Integer versionIsNew;
    }

    @Data
    public static class VersionItem {
        private NiucloudVersion niucloudVersion;
        private String versionNo;
        private String upgradeLog;
        private String releaseTime;
        private Integer niucloudVersionId;
    }

}
