package com.niu.shop.service.core.refund.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.pay.PayRefund;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.entity.order.ShopOrderRefund;
import com.niu.shop.entity.order.ShopOrderRefundLog;
import com.niu.shop.enums.order.*;
import com.niu.shop.event.order.OrderRefundFinishAfterEvent;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.mapper.order.ShopOrderRefundLogMapper;
import com.niu.shop.mapper.order.ShopOrderRefundMapper;
import com.niu.shop.service.core.order.ICoreOrderDeliveryService;
import com.niu.shop.service.core.order.ICoreOrderFinishService;
import com.niu.shop.service.core.order.ICoreOrderService;
import com.niu.shop.service.core.order.param.OrderDeliveryParam;
import com.niu.shop.service.core.refund.ICoreRefundService;
import com.niu.shop.service.core.refund.param.AddRefundLogParam;
import com.niu.shop.service.core.refund.param.RefundFinishParam;
import com.niu.shop.service.core.shop.ICoreStatService;
import com.niu.shop.service.core.shop.param.AddStatParam;
import org.checkerframework.checker.units.qual.A;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CoreRefundServiceImpl implements ICoreRefundService {

    @Resource
    ShopOrderRefundLogMapper shopOrderRefundLogMapper;

    @Resource
    ShopOrderRefundMapper shopOrderRefundMapper;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    ShopOrderMapper shopOrderMapper;

    @Resource
    ICoreStatService coreStatService;

    @Resource
    ICoreOrderDeliveryService coreOrderDeliveryService;

    @Resource
    ICoreOrderService coreOrderService;

    ICoreRefundService coreRefundService;

    @Resource
    public void setCoreRefundService(@Lazy ICoreRefundService coreRefundService) {
        this.coreRefundService = coreRefundService;
    }

    /**
     * 添加维权日志
     *
     * @param data
     */
    @Override
    public void addLog(AddRefundLogParam data) {
        ShopOrderRefundLog model = new ShopOrderRefundLog();
        BeanUtil.copyProperties(data, model);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        shopOrderRefundLogMapper.insert(model);
    }

    /**
     * 退款完成
     *
     * @param param
     */
    @Override
    public void finish(RefundFinishParam param) {
        ShopOrderRefund refund = shopOrderRefundMapper.selectOne(new QueryWrapper<ShopOrderRefund>()
                .eq("order_refund_no", param.getOrderRefundNo())
        );
        if (refund == null) throw new CommonException("退款已失效");
        if (refund.getStatus().equals(OrderRefundStatusEnum.FINISH.getStatus().toString())) {
            throw new CommonException("退款已完成");
        }

        ShopOrderRefund model = new ShopOrderRefund();
        model.setRefundId(refund.getRefundId());
        model.setStatus(OrderRefundStatusEnum.FINISH.getStatus().toString());
        shopOrderRefundMapper.updateById(model);

        ShopOrderGoods orderGoods = new ShopOrderGoods();
        orderGoods.setOrderGoodsId(refund.getOrderGoodsId());
        orderGoods.setStatus(OrderGoodsRefundEnum.REFUND_FINISH.getStatus());
        orderGoods.setIsEnableRefund(0);
        shopOrderGoodsMapper.updateById(orderGoods);

        OrderRefundFinishAfterEvent event = new OrderRefundFinishAfterEvent();
        event.setSiteId(refund.getSiteId());
        event.addAppSign("shop");
        event.setName("OrderRefundFinishAfterEvent");
        event.setRefund(refund);
        EventAndSubscribeOfPublisher.publishAll(event);

        coreRefundService.refundFinishAfter(refund);
    }

    @Async
    @Override
    public void refundFinishAfter(ShopOrderRefund refund) {
        ShopOrder order = shopOrderMapper.selectOne(new QueryWrapper<ShopOrder>()
                .eq("order_id", refund.getOrderId())
                .select("order_id,site_id,status")
        );
        if (order == null) return;

        // 校验订单项是否全部发货
        if (order.getStatus().equals(OrderStatusEnum.WAIT_DELIVERY.getStatus().toString())) {
            OrderDeliveryParam deliveryParam = new OrderDeliveryParam();
            deliveryParam.setOrderId(order.getOrderId());
            deliveryParam.setSiteId(order.getSiteId());
            coreOrderDeliveryService.checkFinish(deliveryParam);
        }
        // 校验订单项是否全部退款
        if (!order.getStatus().equals(OrderStatusEnum.CLOSE.getStatus().toString())) {
            coreOrderService.checkAllClose(order.getOrderId());
        }

        // 操作日志
        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(refund.getOrderRefundNo());
        logParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
        logParam.setMainId(0);
        logParam.setType(OrderRefundActionTypeEnum.FINISH_ACTION.getType());
        logParam.setStatus(Integer.valueOf(refund.getStatus()));
        this.addLog(logParam);

        // 商城统计
        AddStatParam statParam = new AddStatParam();
        statParam.setRefundMoney(refund.getMoney());
        coreStatService.addStat(order.getSiteId(), statParam);
    }

    /**
     * 退款转账完成
     *
     * @param refund
     */
    @Override
    public void refundTransferSuccess(PayRefund refund) {
        ShopOrderRefund orderRefund = shopOrderRefundMapper.selectOne(new QueryWrapper<ShopOrderRefund>()
                .eq("refund_no", refund.getRefundNo())
        );
        if (orderRefund == null) return;

        ShopOrderRefund model = new ShopOrderRefund();
        model.setRefundId(orderRefund.getRefundId());
        model.setTransferTime(System.currentTimeMillis() / 1000);
        shopOrderRefundMapper.updateById(model);

        RefundFinishParam param = new RefundFinishParam();
        param.setOrderRefundNo(orderRefund.getOrderRefundNo());
        this.finish(param);
    }
}
