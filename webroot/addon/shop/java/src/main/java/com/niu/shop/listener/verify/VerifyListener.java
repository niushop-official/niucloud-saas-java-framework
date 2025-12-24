package com.niu.shop.listener.verify;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.component.context.event.DefaultEventResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.event.sys.VerifyEventDefiner;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.order.OrderDeliveryStatusEnum;
import com.niu.shop.enums.order.OrderGoodsRefundEnum;
import com.niu.shop.enums.order.OrderLogMainTypeEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.core.order.ICoreOrderFinishService;
import com.niu.shop.service.core.order.param.OrderFinishParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@EventCallback("shop")
@Component
public class VerifyListener extends VerifyEventDefiner {

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    ShopOrderMapper shopOrderMapper;

    @Resource
    ICoreOrderFinishService coreOrderFinishService;

    @Override
    public DefaultEventResult handleCallback(VerifyEvent event) {
        if (event.getType().equals("shopVirtualGoods")) {
            handleVirtualGoods(event);
        } else if (event.getType().equals("shopPickUpOrder")) {
            handlePickUpOrder(event);
        }
        return null;
    }

    private void handleVirtualGoods(VerifyEvent event) {
        Integer siteId = event.getSiteId();
        Integer orderGoodsId = Integer.valueOf((String) ObjectUtil.defaultIfNull(event.getData().get("order_goods_id"), 0));

        ShopOrderGoods orderGoods = shopOrderGoodsMapper.selectOne(new QueryWrapper<ShopOrderGoods>()
                .eq("order_goods_id", orderGoodsId)
                .eq("site_id", siteId));
        Assert.notNull(orderGoods, "订单不存在");

        // 验证核销状态
        if (orderGoods.getDeliveryStatus().equals(OrderDeliveryStatusEnum.TAKED.getStatus().toString()) ||
                orderGoods.getDeliveryStatus().equals(OrderDeliveryStatusEnum.EXPIRE.getStatus().toString())) {
            throw new CommonException("商品已核销或已过期");
        }

        // 验证退款状态
        if (OrderGoodsRefundEnum.REFUNDING.getStatus().equals(orderGoods.getStatus()) ||
                OrderGoodsRefundEnum.REFUND_FINISH.getStatus().equals(orderGoods.getStatus())) {
            throw new CommonException("当前商品项存在退款");
        }

        // 验证过期时间
        if (orderGoods.getVerifyExpireTime() != null && orderGoods.getVerifyExpireTime() > 0 &&
                orderGoods.getVerifyExpireTime() < DateUtils.currTime()) {
            throw new CommonException("商品已过期");
        }

        // 验证核销次数
        if (orderGoods.getVerifyCount() >= orderGoods.getNum()) {
            throw new CommonException("已达到最大核销次数");
        }

        ShopOrder order = shopOrderMapper.selectById(orderGoods.getOrderId());
        Assert.notNull(order, "订单不存在");

        if (order.getStatus().equals(OrderStatusEnum.FINISH.getStatus().toString()) ||
                order.getStatus().equals(OrderStatusEnum.CLOSE.getStatus().toString())) {
            throw new CommonException("订单已关闭或已完成");
        }

        ShopOrderGoods orderGoodsUpdate = new ShopOrderGoods();
        orderGoodsUpdate.setOrderGoodsId(orderGoodsId);
        orderGoodsUpdate.setVerifyCount(orderGoods.getVerifyCount() + 1);
        shopOrderGoodsMapper.updateById(orderGoodsUpdate);

        // 核销完成 订单完成
        if (orderGoodsUpdate.getVerifyCount() >= orderGoods.getNum()) {
            OrderFinishParam finishParam = new OrderFinishParam();
            finishParam.setOrderId(order.getOrderId());
            finishParam.setSiteId(order.getSiteId());
            finishParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
            finishParam.setMainId(0);
            coreOrderFinishService.finish(finishParam);
        }
    }

    private void handlePickUpOrder(VerifyEvent event) {
        Integer siteId = event.getSiteId();
        Integer orderId = Integer.valueOf((String) ObjectUtil.defaultIfNull(event.getData().get("order_id"), 0));

        ShopOrder order = shopOrderMapper.selectOne(new LambdaQueryWrapper<ShopOrder>()
                .eq(ShopOrder::getOrderId, orderId)
                .eq(ShopOrder::getSiteId, siteId));
        Assert.notNull(order, "订单不存在");

        if (order.getStatus().equals(OrderStatusEnum.FINISH.getStatus().toString()) ||
                order.getStatus().equals(OrderStatusEnum.CLOSE.getStatus().toString())) {
            throw new CommonException("订单已关闭或已完成");
        }

        // 查询所有订单商品
        List<ShopOrderGoods> orderGoodsList = shopOrderGoodsMapper.selectList(new LambdaQueryWrapper<ShopOrderGoods>()
                .eq(ShopOrderGoods::getOrderId, orderId)
                .eq(ShopOrderGoods::getSiteId, siteId));

        // 验证每个商品的核销状态
        for (ShopOrderGoods orderGoods : orderGoodsList) {
            if (orderGoods.getDeliveryStatus().equals(OrderDeliveryStatusEnum.TAKED.getStatus().toString()) ||
                    orderGoods.getDeliveryStatus().equals(OrderDeliveryStatusEnum.EXPIRE.getStatus().toString())) {
                throw new CommonException("商品已核销或已过期");
            }

            if (OrderGoodsRefundEnum.REFUNDING.getStatus().equals(orderGoods.getStatus()) ||
                    OrderGoodsRefundEnum.REFUND_FINISH.getStatus().equals(orderGoods.getStatus())) {
                throw new CommonException("商品正在退款或已退款");
            }
        }

        // 完成订单
        OrderFinishParam finishParam = new OrderFinishParam();
        finishParam.setOrderId(orderId);
        finishParam.setSiteId(siteId);
        finishParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
        finishParam.setMainId(0);
        coreOrderFinishService.finish(finishParam);
    }
}
