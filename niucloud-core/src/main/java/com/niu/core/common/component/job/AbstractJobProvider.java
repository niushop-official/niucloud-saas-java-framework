package com.niu.core.common.component.job;

import cn.hutool.core.util.StrUtil;
import com.niu.core.common.component.job.annotation.JobProvider;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
public abstract class AbstractJobProvider implements Job, IJobProvider {

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
            try {
                this.exec(context);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("任务执行错误：", e);
            }
        }
    }

}
