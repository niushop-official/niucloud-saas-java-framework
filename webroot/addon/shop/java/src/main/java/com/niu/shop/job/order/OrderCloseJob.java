package com.niu.shop.job.order;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.enums.order.OrderCloseTypeEnum;
import com.niu.shop.enums.order.OrderLogMainTypeEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.core.order.ICoreOrderService;
import com.niu.shop.service.core.order.param.OrderCloseParam;
import org.quartz.JobExecutionContext;

import java.util.List;

@JobProvider(key = "OrderCloseJob", name = "订单自动关闭", describe = "订单自动关闭", source = "shop")
public class OrderCloseJob extends AbstractJobProvider implements IJobProvider  {

    private ShopOrderMapper shopOrderMapper() {
        return SpringContext.bean(ShopOrderMapper.class);
    }

    private static ICoreOrderService coreOrderService() {
        return SpringContext.bean(ICoreOrderService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        List<ShopOrder> orderList = shopOrderMapper().selectList(new QueryWrapper<ShopOrder>()
            .eq("status", OrderStatusEnum.WAIT_PAY.getStatus())
            .gt("timeout", 0)
            .le("timeout", System.currentTimeMillis() / 1000)
            .select("order_id,site_id")
        );
        if (ObjectUtil.isNotEmpty(orderList)) {
            for (ShopOrder order: orderList) {
                OrderCloseParam closeParam = new OrderCloseParam();
                closeParam.setOrderId(order.getOrderId());
                closeParam.setSiteId(order.getSiteId());
                closeParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
                closeParam.setMainId(0);
                closeParam.setCloseType(OrderCloseTypeEnum.AUTO_CLOSE.getType());
                coreOrderService().close(closeParam);
            }
        }
    }

}
