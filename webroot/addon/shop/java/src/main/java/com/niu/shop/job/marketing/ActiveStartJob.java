package com.niu.shop.job.marketing;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.shop.entity.active.ShopActive;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.mapper.active.ShopActiveMapper;
import com.niu.shop.service.core.marketing.ICoreActiveService;
import com.niu.shop.service.core.marketing.ICoreCouponMemberService;
import org.quartz.JobExecutionContext;

import java.util.List;

@JobProvider(key = "ActiveStartJob", name = "活动到时自动开启", describe = "活动到时自动开启", source = "shop")
public class ActiveStartJob extends AbstractJobProvider implements IJobProvider {

    private ShopActiveMapper shopActiveMapper() {
        return SpringContext.bean(ShopActiveMapper.class);
    }

    private ICoreActiveService coreActiveService() {
        return SpringContext.bean(ICoreActiveService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        List<ShopActive> activeList = shopActiveMapper().selectList(new QueryWrapper<ShopActive>()
                .eq("active_status", ActiveStatusEnum.NOT_ACTIVE.getStatus())
                .lt("start_time", System.currentTimeMillis() / 1000)
                .select("active_id")
        );
        if (ObjectUtil.isNotEmpty(activeList)) {
            for (ShopActive item: activeList) {
                coreActiveService().start(item.getActiveId());
            }
        }
    }
}
