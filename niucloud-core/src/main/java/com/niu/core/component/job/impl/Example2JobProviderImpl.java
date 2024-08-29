package com.niu.core.component.job.impl;

import com.niu.core.component.job.AbstractJobProvider;
import com.niu.core.component.job.IJobProvider;
import com.niu.core.component.job.annotation.JobProvider;
import org.quartz.JobExecutionContext;

// @JobProvider(key = "example2Job", name = "系统案例任务", describe = "系统案例任务", source = "core")
public class Example2JobProviderImpl extends AbstractJobProvider implements IJobProvider {

    // 这里是为参考案例而提供的

    @Override
    public void exec(JobExecutionContext context) {
        System.out.println(this.getClass() + ",正在执行..................");
    }

}
