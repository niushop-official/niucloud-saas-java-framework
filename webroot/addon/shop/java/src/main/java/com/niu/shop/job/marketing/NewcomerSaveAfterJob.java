package com.niu.shop.job.marketing;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.shop.service.core.marketing.ICoreShopNewcomerService;
import org.quartz.JobExecutionContext;

@SuppressWarnings("unused")
@JobProvider(key = "NewcomerSaveAfterJob", name = "新人专享活动修改后更新会员活动有效期", describe = "新人专享活动修改后更新会员活动有效期", source = "shop")
public class NewcomerSaveAfterJob extends AbstractJobProvider implements IJobProvider {
    private volatile boolean init = false;
    private ICoreShopNewcomerService coreShopNewcomerService;

    @Override
    public void exec(JobExecutionContext context) {
        if (!init) {
            init();
            init = true;
        }

        coreShopNewcomerService.afterSave();
    }

    private void init() {
        coreShopNewcomerService = SpringContext.bean(ICoreShopNewcomerService.class);
    }
}
