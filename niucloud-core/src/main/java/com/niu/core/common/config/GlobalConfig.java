package com.niu.core.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 全局配置
 */
@Configuration
public class GlobalConfig {



    /**
     * 表前缀配置
     */
    public static String tablePrefix = "nc_";

    /**
     * 配置的默认语言包
     */
    public static String defaultLanguage = "zh_CN";

    /**
     * 应用名称
     */
    public static String applicationName;

    /**
     * 运行环境（dev、prod）
     */
    public static String runActive;

    /**
     * 后端域名
     */
    public static String adminDomain;

    /**
     * 手机端域名
     */
    public static String wapDomain;

    /**
     * web端域名
     */
    public static String webDomain;

    /**
     * 版本
     */
    public static String version = "0.0.3";

    /**
     * 应用key
     *
     */
    public static String appKey = "niucloud-admin";

    /**
     * 默认访问路径
     **/
    public static String defaultAccessPath;

    /**
     * 是否是演示站点
     */
    public static Boolean isDemo = false;

    @Value("${mybatis-plus.global-config.db-config.table-prefix}")
    public void setTablePrefix(String tablePrefix) {
        GlobalConfig.tablePrefix = tablePrefix;
    }

    @Value("${spring.name}")
    public void setApplicationName(String applicationName) {
        GlobalConfig.applicationName = applicationName;
    }

    @Value("${spring.web.language}")
    public void setDefaultLanguage(String defaultLanguage) {
        GlobalConfig.defaultLanguage = defaultLanguage;
    }


    @Value("${spring.profiles.active}")
    public void setRunActive(String runActive) {
        if ("dev".equals(runActive)) {
            GlobalConfig.runActive = "dev";
        } else {
            GlobalConfig.runActive = "prod";
        }
    }

    public static String language() {
        return defaultLanguage;
    }

    @Value("${spring.web.default-access-path}")
    public void setDefaultAccessPath(String defaultAccessPath) {
        GlobalConfig.defaultAccessPath = defaultAccessPath;
    }

    public static String defaultAccessPath() {
        return GlobalConfig.defaultAccessPath;
    }

    @Value("${system.admin-domain}")
    public void setAdminDomain(String adminDomain) {
        GlobalConfig.adminDomain = adminDomain;
    }

    @Value("${system.wap-domain}")
    public void setWapDomain(String wapDomain) {
        GlobalConfig.wapDomain = wapDomain;
    }

    @Value("${system.web-domain}")
    public void setWebDomain(String webDomain) {
        GlobalConfig.webDomain = webDomain;
    }

    @Value("${system.is-demo:false}")
    public void setIsDemo(Boolean isDemo) {
        GlobalConfig.isDemo = isDemo;
    }
}
