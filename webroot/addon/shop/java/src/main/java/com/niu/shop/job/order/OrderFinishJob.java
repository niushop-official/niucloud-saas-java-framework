package com.niu.shop.job.order;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.core.order.ICoreOrderFinishService;
import com.niu.shop.service.core.order.param.OrderFinishParam;
import org.quartz.JobExecutionContext;

import java.util.List;

@JobProvider(key = "OrderFinishJob", name = "订单自动收货完成", describe = "订单自动收货完成", source = "shop")
public class OrderFinishJob extends AbstractJobProvider implements IJobProvider  {

    private ShopOrderMapper shopOrderMapper() {
        return SpringContext.bean(ShopOrderMapper.class);
    }

    private static ICoreOrderFinishService coreOrderFinishService() {
        return SpringContext.bean(ICoreOrderFinishService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        List<ShopOrder> orderList = shopOrderMapper().selectList(new QueryWrapper<ShopOrder>()
                .eq("status", OrderStatusEnum.WAIT_TAKE.getStatus())
                .gt("timeout", 0)
                .le("timeout", System.currentTimeMillis() / 1000)
                .select("order_id,site_id")
        );
        if (ObjectUtil.isNotEmpty(orderList)) {
            for (ShopOrder order: orderList) {
                OrderFinishParam finishParam = new OrderFinishParam();
                finishParam.setOrderId(order.getOrderId());
                finishParam.setSiteId(order.getSiteId());
                coreOrderFinishService().finish(finishParam);
            }
        }
    }

}
