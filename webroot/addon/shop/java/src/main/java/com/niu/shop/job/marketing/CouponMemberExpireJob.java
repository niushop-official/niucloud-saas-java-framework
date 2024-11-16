package com.niu.shop.job.marketing;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.shop.service.core.marketing.ICoreCouponMemberService;
import org.quartz.JobExecutionContext;

@JobProvider(key = "CouponMemberExpireJob", name = "优惠券到期自动过期", describe = "优惠券到期自动过期", source = "shop")
public class CouponMemberExpireJob extends AbstractJobProvider implements IJobProvider {

    private ICoreCouponMemberService coreCouponMemberService() {
        return SpringContext.bean(ICoreCouponMemberService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        coreCouponMemberService().expire();
    }
}
