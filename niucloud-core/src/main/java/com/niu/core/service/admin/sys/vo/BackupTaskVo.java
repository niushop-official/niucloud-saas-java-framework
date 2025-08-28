package com.niu.core.service.admin.sys.vo;

import com.niu.core.entity.sys.SysBackupRecords;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

@Data
public class BackupTaskVo {

    public String key;

    private String step;

    private LinkedList<String> executed = new LinkedList<>();

    private Map<String, Object> params;

    private String task;

    private String content;

    private LinkedHashMap<String, Step> steps = new LinkedHashMap<>();

    private SysBackupRecords backupRecord;

    public BackupTaskVo() {
        this.steps = new LinkedHashMap<>();
        steps.put("startBackup", new Step("startBackup", "开始备份"));
        steps.put("backupCode", new Step("backupCode", "备份源码"));
        steps.put("backupSql", new Step("backupSql", "备份数据库"));
        steps.put("backupComplete", new Step("backupComplete", "备份完成"));
    }

    @Data
    @AllArgsConstructor
    public static class Step {
        private String step;

        private String title;
    }
}
