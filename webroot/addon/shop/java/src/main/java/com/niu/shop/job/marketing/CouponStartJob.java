package com.niu.shop.job.marketing;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.shop.entity.coupon.ShopCoupon;
import com.niu.shop.enums.coupon.CouponStatusEnum;
import com.niu.shop.mapper.coupon.ShopCouponMapper;
import com.niu.shop.service.core.marketing.ICoreCouponService;
import org.quartz.JobExecutionContext;

import java.util.List;
import java.util.stream.Collectors;

@JobProvider(key = "CouponStartJob", name = "优惠券到时自动开启", describe = "优惠券到时自动开启", source = "shop")
public class CouponStartJob extends AbstractJobProvider implements IJobProvider {

    private ShopCouponMapper shopCouponMapper() {
        return SpringContext.bean(ShopCouponMapper.class);
    }

    private ICoreCouponService coreCouponService() {
        return SpringContext.bean(ICoreCouponService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        List<ShopCoupon> list = shopCouponMapper().selectList(new QueryWrapper<ShopCoupon>()
                .eq("status", CouponStatusEnum.WAIT_START.getStatus())
                .gt("start_time", 0)
                .lt("start_time", System.currentTimeMillis() / 1000)
                .gt("end_time", System.currentTimeMillis() / 1000)
                .select("id")
        );
        if (ObjectUtil.isNotEmpty(list)) {
            List<Integer> ids = list.stream().map(i -> i.getId()).collect(Collectors.toList());
            coreCouponService().couponNormal(ids);
        }
    }
}
