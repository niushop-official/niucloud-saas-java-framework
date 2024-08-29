package com.niu.core.job.prototype;

import com.niu.core.component.job.AbstractJobProvider;
import com.niu.core.component.job.IJobProvider;
import com.niu.core.component.job.annotation.JobProvider;
import org.quartz.JobExecutionContext;

@JobProvider(key = "prototypeJob", name = "原型任务案例", describe = "原型任务案例", source = "core")
public class PrototypeJob extends AbstractJobProvider implements IJobProvider {

    @Override
    public void exec(JobExecutionContext context) {
        // 业务逻辑
        System.out.println("原型任务案例..................业务逻辑实现..................");
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
