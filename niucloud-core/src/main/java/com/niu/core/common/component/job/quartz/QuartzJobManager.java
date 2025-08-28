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
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
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
        String sec = timeMap.getOrDefault("sec", "1");
        String min = timeMap.getOrDefault("min", "0");
        String hour = timeMap.getOrDefault("hour", "0");
        String day = timeMap.getOrDefault("day", "1");
        String week = timeMap.getOrDefault("week", "*");
        String type = timeMap.getOrDefault("type", "");

        // 确保 day 是合法数字
        if (!day.matches("\\d+") || day.equals("0")) {
            day = "1"; // 默认每1天执行一次
        }

        switch (type) {
            case "sec":
                return "*/" + sec + " * * * * ?";
            case "min":
                return "0 */" + min + " * * * ?";
            case "hour":
                return "0 " + min + " */" + hour + " * * ?";
            case "day":
                // 修正：使用 "0 min hour */day * ?"
                return "0 " + min + " " + hour + " */" + day + " * ?";
            case "week":
                // week 应为 MON, TUE 等，或数字（需映射）
                String dayOfWeek = week;
                if (week.equals("*")) {
                    dayOfWeek = "MON"; // 默认周一
                } else if (week.matches("\\d")) {
                    String[] days = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
                    int idx = Integer.parseInt(week) % 7;
                    dayOfWeek = days[idx];
                }
                return "0 " + min + " " + hour + " ? * " + dayOfWeek;
            case "month":
                // day 必须是 1-31 的数字
                if (!day.matches("\\d+") || Integer.parseInt(day) < 1 || Integer.parseInt(day) > 31) {
                    day = "1";
                }
                return "0 " + min + " " + hour + " " + day + " * ?";
            default:
                return null; // 或抛出异常
        }
    }

    /**
     * @param timeMap
     * @return
     */
    public static String toCronContent(Map<String, String> timeMap) {
        String sec = timeMap.getOrDefault("sec", "*");
        String min = timeMap.getOrDefault("min", "*");
        String hour = timeMap.getOrDefault("hour", "*");
        String day = timeMap.getOrDefault("day", "*");
        String week = timeMap.getOrDefault("week", "*");
        String type = timeMap.getOrDefault("type", "");

        switch (type) {
            case "sec":
                return String.format("每%s秒执行一次", sec.equals("*") ? "1" : sec);
            case "min":
                if (min.equals("*")) {
                    return "每分钟执行一次";
                }
                return String.format("每%s分钟执行一次", min);
            case "hour":
                if (hour.equals("*")) {
                    return String.format("每小时的第%s分钟执行", min.equals("*") ? "0" : min);
                }
                return String.format("每%s小时的第%s分钟执行", hour, min.equals("*") ? "0" : min);
            case "day":
                if (day.equals("*")) {
                    return String.format("每天%s点%s分执行", hour.equals("*") ? "0" : hour, min.equals("*") ? "0" : min);
                }
                return String.format("每%s天的%s点%s分执行", day, hour.equals("*") ? "0" : hour, min.equals("*") ? "0" : min);
            case "week":
                String[] weekDays = {"日", "一", "二", "三", "四", "五", "六"};
                String dayOfWeek;
                if (week.equals("*")) {
                    dayOfWeek = "日";
                } else {
                    try {
                        int weekNum = Integer.parseInt(week);
                        dayOfWeek = weekDays[weekNum % 7];
                    } catch (NumberFormatException e) {
                        dayOfWeek = week;
                    }
                }
                return String.format("每周%s的%s点%s分执行", dayOfWeek, hour.equals("*") ? "0" : hour, min.equals("*") ? "0" : min);
            case "month":
                if (day.equals("*")) {
                    return String.format("每月1日的%s点%s分执行", hour.equals("*") ? "0" : hour, min.equals("*") ? "0" : min);
                }
                return String.format("每月%s日的%s点%s分执行", day, hour.equals("*") ? "0" : hour, min.equals("*") ? "0" : min);
            default:
                return "未配置执行时间";
        }
    }

    public static String getClassPath(String basePackage, String className) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            // 构建搜索模式
            String pattern = "classpath*:" +
                    basePackage.replace('.', '/') + "/**/" +
                    className + ".class";

            // 查找资源
            Resource[] resources = resolver.getResources(pattern);
            if (resources.length == 0) {
                return null;
            }

            String rawPath = resources[0].getURL().toString();

            // 处理生产环境(JAR包)
            if (rawPath.startsWith("jar:")) {
                String jarPath = rawPath.substring("jar:file:".length());
                jarPath = jarPath.substring(0, jarPath.indexOf("!"));
                String classEntry = rawPath.substring(rawPath.indexOf("!") + 2)
                        .replace(".class", "")
                        .replace('/', '.');
                return jarPath + " | " + classEntry;
            }
            // 处理开发环境
            else if (rawPath.startsWith("file:")) {
                String devPath = rawPath.substring("file:".length());
                // 移除项目路径前缀，保留从classes开始的路径
                int classesIndex = devPath.indexOf("/target/classes/");
                if (classesIndex > 0) {
                    return devPath.substring(classesIndex + "/target/classes/".length())
                            .replace(".class", "").replace("/", ".");
                }
                return devPath.replace(".class", "").replace("/", ".");
            }
            // 其他情况
            return rawPath.replace(".class", "").replace("/", ".");

        } catch (IOException e) {
            log.error("查找类路径失败 - package: {}, class: {}, error: {}",
                    basePackage, className, e.getMessage());
            return null;
        }
    }

}
