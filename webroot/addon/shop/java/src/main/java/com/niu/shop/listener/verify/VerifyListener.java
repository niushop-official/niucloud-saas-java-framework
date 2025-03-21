package com.niu.shop.listener.verify;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.component.context.event.DefaultEventResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.event.sys.VerifyEventDefiner;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.order.OrderDeliveryStatusEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.core.order.ICoreOrderFinishService;
import com.niu.shop.service.core.order.param.OrderFinishParam;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;

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
            Integer siteId = event.getSiteId();
            Integer orderGoodsId = Integer.valueOf((String) ObjectUtil.defaultIfNull(event.getData().get("order_goods_id"), 0));

            ShopOrderGoods orderGoods = shopOrderGoodsMapper.selectOne(new QueryWrapper<ShopOrderGoods>().eq("order_goods_id", orderGoodsId).eq("site_id", siteId));
            Assert.notNull(orderGoods, "订单不存在");

            if (orderGoods.getDeliveryStatus().equals(OrderDeliveryStatusEnum.TAKED.getStatus().toString()) || orderGoods.getDeliveryStatus().equals(OrderDeliveryStatusEnum.EXPIRE.getStatus().toString())) {
                throw new CommonException("商品已核销或已过期");
            }

            ShopOrder order = shopOrderMapper.selectById(orderGoods.getOrderId());
            Assert.notNull(order, "订单不存在");

            if (order.getStatus().equals(OrderStatusEnum.FINISH.getStatus().toString()) || order.getStatus().equals(OrderStatusEnum.CLOSE.getStatus().toString())) {
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
                coreOrderFinishService.finish(finishParam);
            }
        }
        return null;
    }
}
