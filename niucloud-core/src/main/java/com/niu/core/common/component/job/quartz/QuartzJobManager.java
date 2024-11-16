package com.niu.core.common.component.job.quartz;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.factory.JobProviderFactory;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.entity.sys.SysSchedule;
import com.niu.core.service.admin.sys.ISysScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.LinkedMap;

import java.util.List;
import java.util.Map;

@Slf4j
public class QuartzJobManager {

    /**
     * 初始化
     */
    public static void init() {
        QuartzManager.initSchedulerFactory();
        ISysScheduleService sysScheduleService = SpringContext.bean(ISysScheduleService.class);
        List<SysSchedule> sysScheduleList = sysScheduleService.getSysEnableList();
        if (sysScheduleList != null && sysScheduleList.size() > 0) {
            for (SysSchedule sysSchedule : sysScheduleList) {
                startJob(sysSchedule);
            }
        }
    }

    /**
     * 启动任务
     *
     * @param sysSchedule
     */
    public static void startJob(SysSchedule sysSchedule) {
        String jobName = sysSchedule.getKey();
        String jobGroupName = "default";
        String triggerName = sysSchedule.getKey();
        String triggerGroupName = "default";
        Class<? extends IJobProvider> jobClass = JobProviderFactory.getJob(sysSchedule.getKey());
        if (jobClass != null) {
            Map<String, String> mapResult = JSON.parseObject(sysSchedule.getTime(), LinkedMap.class);
            String cron = "";
            try {
                cron = toCronStr(mapResult);
            } catch (Exception e) {
                // ignore
            }
            if (StrUtil.isNotEmpty(cron)) {
                QuartzManager.addJob(jobName, jobGroupName, triggerName, triggerGroupName, jobClass, cron);
            }
        }
    }

    /**
     * @param timeJsonString
     * @return
     */
    public static String convertCronContent(String timeJsonString) {
        try {
            Map<String, String> mapDataResult = JSON.parseObject(timeJsonString, LinkedMap.class);
            return toCronContent(mapDataResult);
        } catch (Exception e) {
            // ignore
        }
        return "";
    }

    /**
     * 停止任务
     *
     * @param jobKey
     */
    public static void stopJob(String jobKey) {
        String jobName = jobKey;
        String jobGroupName = "default";
        String triggerName = jobKey;
        String triggerGroupName = "default";
        QuartzManager.removeJob(jobName, jobGroupName, triggerName, triggerGroupName);
    }

    /**
     * 停止任务
     *
     * @param sysSchedule
     */
    public static void stopJob(SysSchedule sysSchedule) {
        stopJob(sysSchedule.getKey());
    }

    /**
     * @param timeMap
     * @return
     */
    public static String toCronStr(Map<String, String> timeMap) {
        String crontab = "";
        String sec = "*";
        if (timeMap.get("sec") != null) {
            sec = timeMap.get("sec");
        }
        String min = "*";
        if (timeMap.get("min") != null) {
            min = timeMap.get("min");
        }
        String hour = "*";
        if (timeMap.get("hour") != null) {
            hour = timeMap.get("hour");
        }
        String day = "*";
        if (timeMap.get("day") != null) {
            day = timeMap.get("day");
        }
        String week = "*";
        if (timeMap.get("week") != null) {
            week = timeMap.get("week");
        }
        String type = "";
        if (timeMap.get("type") != null) {
            type = timeMap.get("type");
        }
        switch (type) {
            case "sec":
                // 每隔几秒执行
                crontab = "*/" + sec + " * * * * ?";
                break;
            case "min":
                // 每隔几分执行
                crontab = "0 */" + min + " * * * ?";
                break;
            case "hour":
                // 每隔几小时
                crontab = "0 " + min + " */" + hour + " * * ?";
                break;
            case "day":
                // 每隔几日第几小时第几分钟执行
                crontab = "0 " + min + " " + hour + " */" + day + " * ?";
                break;
            case "week":
                // 每周一次,周几具体时间执行
                crontab = "0 " + min + " " + hour + " ? * " + week;
                break;
            case "month":
                // 每月一次,某日具体时间执行
                crontab = "0 " + min + " " + hour + " " + day + " * ?";
                break;
        }
        return crontab;
    }

    /**
     * @param timeMap
     * @return
     */
    public static String toCronContent(Map<String, String> timeMap) {
        String cronContent = "";
        String sec = "*";
        if (timeMap.get("sec") != null) {
            sec = timeMap.get("sec");
        }
        String min = "*";
        if (timeMap.get("min") != null) {
            min = timeMap.get("min");
        }
        String hour = "*";
        if (timeMap.get("hour") != null) {
            hour = timeMap.get("hour");
        }
        String day = "*";
        if (timeMap.get("day") != null) {
            day = timeMap.get("day");
        }
        String week = "*";
        if (timeMap.get("week") != null) {
            week = timeMap.get("week");
        }
        String type = "";
        if (timeMap.get("type") != null) {
            type = timeMap.get("type");
        }
        switch (type) {
            case "sec":
                // 每隔几秒执行
                cronContent = "每隔" + sec + "秒执行一次";
                break;
            case "min":
                // 每隔几分执行
                cronContent = "每隔" + min + "分执行一次";
                break;
            case "hour":
                // 每隔几小时
                cronContent = "每隔" + min + "分" + hour + "小时执行一次";
                break;
            case "day":
                // 每隔几日第几小时第几分钟执行
                cronContent = "每隔" + min + "分" + hour + "小时" + day + "天执行一次";
                break;
            case "week":
                // 每周一次,周几具体时间执行
                cronContent = "每周" + week + " " + min + "分" + hour + "小时执行";
                break;
            case "month":
                // 每月一次,某日具体时间执行
                cronContent = "每月" + day + "日 " + min + "分" + hour + "小时执行";
                break;
        }
        return cronContent;
    }


}
