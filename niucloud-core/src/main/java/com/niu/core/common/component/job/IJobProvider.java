package com.niu.core.common.component.job;

import org.quartz.JobExecutionContext;

public interface IJobProvider {

    /**
     * 执行任务
     *
     * @param context
     */
    public void exec(JobExecutionContext context);

}
