package com.niu.shop.job.order;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.entity.order.ShopOrderLog;
import com.niu.shop.enums.order.OrderCloseTypeEnum;
import com.niu.shop.enums.order.OrderLogMainTypeEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.core.order.ICoreOrderLogService;
import com.niu.shop.service.core.order.ICoreOrderService;
import com.niu.shop.service.core.order.param.OrderCloseParam;
import org.quartz.JobExecutionContext;

import java.util.ArrayList;
import java.util.List;

@JobProvider(key = "OrderCloseAllowRefundJob", name = "订单完成自动关闭售后", describe = "订单完成自动关闭售后", source = "shop")
public class OrderCloseAllowRefundJob extends AbstractJobProvider implements IJobProvider  {

    private ShopOrderMapper shopOrderMapper() {
        return SpringContext.bean(ShopOrderMapper.class);
    }

    private ShopOrderGoodsMapper shopOrderGoodsMapper() {
        return SpringContext.bean(ShopOrderGoodsMapper.class);
    }

    private ICoreOrderLogService coreOrderLogService() {
        return SpringContext.bean(ICoreOrderLogService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        long currTime = DateUtils.currTime();
        List<ShopOrder> orderList = shopOrderMapper().selectList(new LambdaQueryWrapper<ShopOrder>()
                .eq(ShopOrder::getStatus, OrderStatusEnum.FINISH.getStatus())
                .le(ShopOrder::getTimeout, currTime)
                .eq(ShopOrder::getIsEnableRefund, 1));
        if (orderList.isEmpty()) {
            return;
        }
        List<Integer> orderIds = orderList.stream().map(ShopOrder::getOrderId).toList();
        shopOrderMapper().update(new LambdaUpdateWrapper<ShopOrder>()
                .in(ShopOrder::getOrderId, orderIds)
                .set(ShopOrder::getIsEnableRefund, 0)
                .set(ShopOrder::getTimeout, 0));
        shopOrderGoodsMapper().update(new LambdaUpdateWrapper<ShopOrderGoods>()
                .in(ShopOrderGoods::getOrderId, orderIds)
                .set(ShopOrderGoods::getIsEnableRefund, 0));
        List<ShopOrderLog> shopOrderLogs = new ArrayList<>();
        for (ShopOrder shopOrder : orderList) {
            shopOrderLogs.add( ShopOrderLog.builder()
                    .orderId(shopOrder.getOrderId())
                    .mainType(OrderLogMainTypeEnum.SYSTEM.getType())
                    .mainId(0)
                    .status(OrderStatusEnum.FINISH.getStatus())
                    .type(OrderCloseTypeEnum.ORDER_CLOSE_ALLOW_REFUND_ACTION.getType())
                    .content("")
                    .createTime(currTime)
                    .build());
        }
        coreOrderLogService().addBatch(shopOrderLogs);
    }

}
