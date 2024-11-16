package com.niu.core.service.core.schedule;

public interface ICoreScheduleService {

    /**
     * 安装系统内置计划任务
     *
     */
    void installSystemSchedule();

    /**
     * 卸载系统内置计划任务
     *
     */
    void uninstallSystemSchedule();

    /**
     * 安装插件内置计划任务
     *
     */
    void installAddonSchedule(String addon);

    /**
     * 卸载插件内置计划任务
     *
     * @param addon
     */
    void uninstallAddonSchedule(String addon);
}
