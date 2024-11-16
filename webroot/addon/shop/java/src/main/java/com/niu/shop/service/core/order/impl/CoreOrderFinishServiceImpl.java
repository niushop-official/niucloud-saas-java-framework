package com.niu.shop.service.core.order.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.order.OrderDeliveryStatusEnum;
import com.niu.shop.enums.order.OrderGoodsRefundEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.event.order.OrderFinishAfterEvent;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.core.order.ICoreOrderEventService;
import com.niu.shop.service.core.order.ICoreOrderFinishService;
import com.niu.shop.service.core.order.param.OrderFinishParam;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CoreOrderFinishServiceImpl implements ICoreOrderFinishService {

    @Resource
    ShopOrderMapper shopOrderMapper;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    ICoreOrderEventService coreOrderEventService;

    @Override
    public boolean finish(OrderFinishParam param) {

        ShopOrder shopOrder = shopOrderMapper.selectOne(new QueryWrapper<ShopOrder>().eq("site_id", param.getSiteId()).eq("order_id", param.getOrderId()));
        if(ObjectUtil.isEmpty(shopOrder)) throw new CommonException("订单信息不存在");

        if(!shopOrder.getStatus().equals(OrderStatusEnum.WAIT_TAKE.getStatus().toString())) throw new CommonException("只有待收货订单才能完成");
        if(shopOrder.getStatus().equals(OrderStatusEnum.FINISH.getStatus().toString())) throw new CommonException("订单已完成");
        Long refundCount = shopOrderGoodsMapper.selectCount(new QueryWrapper<ShopOrderGoods>().eq("order_id", param.getOrderId()).eq("status", OrderGoodsRefundEnum.REFUNDING.getStatus()));
        if(refundCount > 0) throw new CommonException("订单中存在退款项，无法完成");

        //执行订单完成
        shopOrder.setStatus(OrderStatusEnum.FINISH.getStatus().toString());
        shopOrder.setFinishTime(System.currentTimeMillis() / 1000);
        shopOrder.setTimeout(0L);
        shopOrderMapper.updateById(shopOrder);

        //执行订单项目收货
        this.orderGoodsTake(param.getOrderId(), null);

        //执行订单事件
        coreOrderEventService.orderFinishAfter(shopOrder, param);

        // 订单完成之后事件
        OrderFinishAfterEvent event = new OrderFinishAfterEvent();
        event.setOrder(shopOrder);
        event.setSiteId(shopOrder.getSiteId());
        event.addAppSign("shop");
        event.setName("OrderFinishAfterEvent");
        EventAndSubscribeOfPublisher.publishAll(event);

        return true;
    }

    @Override
    public boolean orderGoodsTake(Integer orderId, Integer[] orderGoodsId) {

        QueryWrapper<ShopOrderGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        queryWrapper.eq("status", OrderGoodsRefundEnum.NORMAL.getStatus());
        if(ObjectUtil.isNotEmpty(orderGoodsId)) queryWrapper.in("order_goods_id", orderGoodsId);

        ShopOrderGoods shopOrderGoods = new ShopOrderGoods();
        shopOrderGoods.setDeliveryStatus(OrderDeliveryStatusEnum.TAKED.getStatus());
        shopOrderGoodsMapper.update(shopOrderGoods, queryWrapper);
        return true;
    }

    @Override
    public void orderShippingNotifyConfirmReceive(Integer siteId, String outTradeNo, String deliveryType) {

    }
}
