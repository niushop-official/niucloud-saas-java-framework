package com.niu.core.common.component.job;

import cn.hutool.core.util.StrUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.common.component.job.quartz.QuartzJobManager;
import com.niu.core.entity.sys.SysScheduleLog;
import com.niu.core.service.admin.sys.ISysScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
public abstract class AbstractJobProvider implements Job, IJobProvider {


    private ISysScheduleService sysScheduleService() {
        return SpringContext.bean(ISysScheduleService.class);
    }

    /**
     * @return
     */
    protected JobProvider getJobProvider() {
        return this.getClass().getAnnotation(JobProvider.class);
    }

    /**
     * @return
     */
    protected String getKey() {
        if (getJobProvider() != null) {
            return getJobProvider().key();
        }
        return "";
    }

    /**
     * @return
     */
    protected String getName() {
        if (getJobProvider() != null) {
            return getJobProvider().name();
        }
        return "";
    }

    /**
     * @return
     */
    protected String getDescribe() {
        if (getJobProvider() != null) {
            return getJobProvider().describe();
        }
        return "";
    }

    protected String getAddon() {
        if (getJobProvider() != null) {
            return getJobProvider().source();
        }
        return "";
    }

    /**
     * 验证
     *
     * @return
     */
    protected boolean validate() {
        String key = this.getKey();
        String name = this.getName();
        if (StrUtil.isNotEmpty(key) && StrUtil.isNotEmpty(name)) {
            return true;
        }
        return false;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if (this.validate()) {
            String key = this.getKey();
            String name = this.getName();
            String addon = this.getAddon();
            String basePackage = String.format("com.niu.%s.job", addon);
            String jobClass = QuartzJobManager.getClassPath(basePackage, key);
            try {
                this.exec(context);
                String result = "计划任务：" + name + "执行成功";
                sysScheduleService().addLog(new SysScheduleLog().builder()
                        .key(key)
                        .name(name)
                        .addon(addon)
                        .className(jobClass)
                        .status("success")
                        .executeResult(result)
                        .job(jobClass).build());
            } catch (Exception e) {
                String errorMsg = "计划任务：" + name + "发生错误, 错误原因：" + e.getMessage() +
                        " at " + e.getStackTrace()[0].toString();
                sysScheduleService().addLog(new SysScheduleLog().builder()
                        .key(key)
                        .name(name)
                        .addon(addon)
                        .className(jobClass)
                        .status("error")
                        .executeResult(errorMsg)
                        .job(jobClass).build());
                log.error("Error executing job: {} ({}), {}", jobClass, name, errorMsg, e);
            }
        }
    }

}

