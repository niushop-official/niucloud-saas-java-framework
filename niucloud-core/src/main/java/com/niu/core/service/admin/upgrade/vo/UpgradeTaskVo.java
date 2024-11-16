package com.niu.core.service.admin.upgrade.vo;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
public class UpgradeTaskVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String upgradeTime;

    private String addon;

    private String key;

    private Map<String, Object> upgrade;

    private String step;

    private List<String> executed = new LinkedList<>();

    private List<String> log = new LinkedList<>();

    private Map<String, Object> params;

    private JSONObject upgradeContent;

    private String status;

    private Map<String, Step> steps = new HashMap<>();


    public UpgradeTaskVo() {
        this.steps = new HashMap<>();
        steps.put("requestUpgrade", new Step("requestUpgrade", "请求升级"));
        steps.put("downloadFile", new Step("downloadFile", "下载更新文件"));
        steps.put("backupCode", new Step("backupCode", "备份源码"));
        steps.put("backupSql", new Step("backupSql", "备份数据库"));
        steps.put("coverCode", new Step("coverCode", "合并更新文件"));
        steps.put("handleUpgrade", new Step("coverCode", "执行升级sql和升级方法"));
        steps.put("handleVue", new Step("handleUniapp", "处理前端源码"));
        steps.put("refreshMenu", new Step("refreshMenu", "刷新菜单"));
        steps.put("installSchedule", new Step("installSchedule", "安装计划任务"));
        steps.put("upgradeComplete", new Step("upgradeComplete", "升级完成"));
    }

    @Data
    @AllArgsConstructor
    public static class Step {
        private String step;

        private String title;
    }
}
