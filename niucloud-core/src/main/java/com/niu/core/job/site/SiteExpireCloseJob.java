package com.niu.core.job.site;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.service.core.site.ICoreSiteService;
import org.quartz.JobExecutionContext;

@JobProvider(key = "SiteExpireCloseJob", name = "站点到期自动关闭", describe = "站点到期自动关闭", source = "core")
public class SiteExpireCloseJob extends AbstractJobProvider implements IJobProvider {

    private ICoreSiteService coreSiteService() {
        return SpringContext.bean(ICoreSiteService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        coreSiteService().siteExpireClose();
    }
}
