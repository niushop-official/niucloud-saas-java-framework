package com.niu.shop.service.core.order.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.object.ObjectUtils;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderDelivery;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import com.niu.shop.enums.order.OrderDeliveryStatusEnum;
import com.niu.shop.enums.order.OrderDeliveryTypeEnum;
import com.niu.shop.enums.order.OrderGoodsRefundEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.event.order.OrderDeliveryAfterEvent;
import com.niu.shop.event.order.OrderPayAfterEvent;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderDeliveryMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.core.order.ICoreOrderDeliveryService;
import com.niu.shop.service.core.order.ICoreOrderEventService;
import com.niu.shop.service.core.order.ICoreOrderFinishService;
import com.niu.shop.service.core.order.param.OrderDeliveryDataParam;
import com.niu.shop.service.core.order.param.OrderDeliveryParam;
import com.niu.shop.service.core.order.param.OrderFinishParam;
import net.sf.ehcache.util.SetAsList;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoreOrderDeliveryServiceImpl implements ICoreOrderDeliveryService {

    @Resource
    ShopOrderMapper shopOrderMapper;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    ICoreOrderFinishService orderFinishService;

    @Resource
    ShopOrderDeliveryMapper shopOrderDeliveryMapper;

    ICoreOrderEventService coreOrderEventService;

    @Resource
    public void setOrderFinishService(@Lazy ICoreOrderFinishService orderFinishService) {
        this.orderFinishService = orderFinishService;
    }

    @Resource
    public void setCoreOrderEventService(@Lazy ICoreOrderEventService coreOrderEventService) {
        this.coreOrderEventService = coreOrderEventService;
    }

    @Override
    public void delivery(OrderDeliveryParam orderDeliveryParam) {

        Integer orderId = orderDeliveryParam.getOrderId();
        Integer siteId = orderDeliveryParam.getSiteId();
        Integer[] orderGoodsIds = orderDeliveryParam.getOrderGoodsIds();
        ShopOrder shopOrder = shopOrderMapper.selectOne(new QueryWrapper<ShopOrder>()
                        .eq("order_id", orderId)
                        .eq("site_id", siteId));
        if(ObjectUtil.isEmpty(shopOrder)) throw new CommonException("订单信息不能为空");

        if(!shopOrder.getStatus().equals(OrderStatusEnum.WAIT_DELIVERY.getStatus().toString())) throw new CommonException("订单状态不是待发货状态");
        List<ShopOrderGoods> orderGoodsData = shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>()
                .eq("order_id", orderId)
                .in("order_goods_id",  Arrays.asList(orderGoodsIds))
                .eq("status", OrderGoodsRefundEnum.NORMAL.getStatus())
                .eq("delivery_status", OrderDeliveryStatusEnum.WAIT_DELIVERY.getStatus()));

        if(orderGoodsData.size() != orderGoodsIds.length) throw new CommonException("订单中存在已退款的商品，不能发货");

        List<String> hasGoodsTypeArray = new ArrayList<>();
        for (ShopOrderGoods orderGoods : orderGoodsData) {
            if(!hasGoodsTypeArray.contains(orderGoods.getGoodsType())) hasGoodsTypeArray.add(orderGoods.getGoodsType());
        }

        if(hasGoodsTypeArray.size() >1) throw new CommonException("一次发货只能发送一种商品类型的订单项");
        String goodsType = hasGoodsTypeArray.get(0);

        OrderDeliveryDataParam orderDeliveryDataParam = new OrderDeliveryDataParam();
        orderDeliveryDataParam.setOrderData(shopOrder);
        orderDeliveryDataParam.setOrderGoodsData(orderGoodsData);
        orderDeliveryDataParam.setParam(orderDeliveryParam);

        if(ObjectUtil.isNotEmpty(goodsType)){
            if(goodsType.equals(GoodsTypeEnum.VIRTUAL.getType()))
            {
                if(!orderDeliveryParam.getDeliveryType().equals(OrderDeliveryTypeEnum.VIRTUAL.getType())) throw new CommonException("虚拟商品只支持虚拟发货");
                this.virtual(orderDeliveryDataParam);
            }else{
               if(!OrderDeliveryTypeEnum.getMap().containsKey(orderDeliveryParam.getDeliveryType())) throw new CommonException("不支持的配送方式");

               //物流配送
               if(shopOrder.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType()))
                {
                    this.express(orderDeliveryDataParam);
                }

               //同城配送
                if(shopOrder.getDeliveryType().equals(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType()))
                {
                    this.localDelivery(orderDeliveryDataParam);
                }

                //门店自提
                if(shopOrder.getDeliveryType().equals(OrderDeliveryTypeEnum.STORE.getType()))
                {
                    this.store(orderDeliveryDataParam);
                }

            }
            this.checkFinish(orderDeliveryParam);
        }


    }

    @Override
    public void virtual(OrderDeliveryDataParam orderDeliveryDataParam) {

        ShopOrder orderData = orderDeliveryDataParam.getOrderData();
        List<ShopOrderGoods> orderGoodsData = orderDeliveryDataParam.getOrderGoodsData();
        OrderDeliveryParam orderDeliveryParam = orderDeliveryDataParam.getParam();
        String deliveryType = orderDeliveryParam.getDeliveryType();

        //添加包裹
        ShopOrderDelivery shopOrderDelivery = new ShopOrderDelivery();
        shopOrderDelivery.setSiteId(orderData.getSiteId());
        shopOrderDelivery.setOrderId(orderData.getOrderId());
        shopOrderDelivery.setDeliveryType(orderData.getDeliveryType());
        shopOrderDelivery.setSubDeliveryType(deliveryType);
        shopOrderDelivery.setRemark(orderDeliveryParam.getRemark());
        Integer deliveryId = this.packages(shopOrderDelivery);

        //虚拟商品自动收货
        for (ShopOrderGoods orderGoods : orderGoodsData)
        {

            orderGoods.setDeliveryStatus(OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus());
            orderGoods.setDeliveryId(deliveryId);
            shopOrderGoodsMapper.updateById(orderGoods);
        }

        //调用虚拟配送(生成核销码)
        List<Integer> goodsIds = orderGoodsData.stream().map(ShopOrderGoods::getGoodsId).collect(Collectors.toList());
        List<ShopGoods> tempGoodsList = shopGoodsMapper.selectList(new QueryWrapper<ShopGoods>().in("goods_id", goodsIds));
        for (ShopOrderGoods orderGoods : orderGoodsData)
        {
            for (ShopGoods shopGoods : tempGoodsList)
            {
                if (orderGoods.getGoodsId().equals(shopGoods.getGoodsId()))
                {
                    if(shopGoods.getVirtualReceiveType().equals("verify"))
                    {
                        //生成核销码
                        Long expireTime;
                        if(shopGoods.getVirtualVerifyType() == 0)
                        {
                            expireTime = 0L;
                        }else if(shopGoods.getVirtualVerifyType() == 1){

                            expireTime = System.currentTimeMillis() / 1000 + shopGoods.getVirtualIndate() * 86400;
                        }else {
                            expireTime = (long)shopGoods.getVirtualIndate();
                        }
                        orderGoods.setVerifyExpireTime(expireTime);
                        orderGoods.setIsVerify(1);
                        shopOrderGoodsMapper.updateById(orderGoods);
                    }else if(shopGoods.getVirtualReceiveType().equals("auto"))
                    {
                        orderFinishService.orderGoodsTake(orderGoods.getOrderId(), new Integer[]{orderGoods.getOrderGoodsId()});
                    }

                }
            }
        }



    }

    @Override
    public Integer packages(ShopOrderDelivery shopOrderDelivery) {
        shopOrderDelivery.setCreateTime(System.currentTimeMillis() / 1000);
        shopOrderDeliveryMapper.insert(shopOrderDelivery);
        return shopOrderDelivery.getId();
    }

    @Override
    public void express(OrderDeliveryDataParam orderDeliveryDataParam) {

        ShopOrder orderData = orderDeliveryDataParam.getOrderData();
        List<ShopOrderGoods> orderGoodsData = orderDeliveryDataParam.getOrderGoodsData();
        OrderDeliveryParam orderDeliveryParam = orderDeliveryDataParam.getParam();
        String deliveryType = orderDeliveryParam.getDeliveryType();

        //添加包裹
        ShopOrderDelivery shopOrderDelivery = new ShopOrderDelivery();
        shopOrderDelivery.setSiteId(orderData.getSiteId());
        shopOrderDelivery.setOrderId(orderData.getOrderId());
        shopOrderDelivery.setDeliveryType(orderData.getDeliveryType());
        shopOrderDelivery.setSubDeliveryType(deliveryType);
        shopOrderDelivery.setRemark(orderDeliveryParam.getRemark());
        shopOrderDelivery.setExpressCompanyId(orderDeliveryParam.getExpressCompanyId());
        shopOrderDelivery.setExpressNumber(orderDeliveryParam.getExpressNumber());
        Integer deliveryId = this.packages(shopOrderDelivery);

        for (ShopOrderGoods orderGoods : orderGoodsData)
        {

            orderGoods.setDeliveryStatus(OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus());
            orderGoods.setDeliveryId(deliveryId);
            shopOrderGoodsMapper.updateById(orderGoods);
        }
    }

    @Override
    public void localDelivery(OrderDeliveryDataParam orderDeliveryDataParam) {

        ShopOrder orderData = orderDeliveryDataParam.getOrderData();
        List<ShopOrderGoods> orderGoodsData = orderDeliveryDataParam.getOrderGoodsData();
        OrderDeliveryParam orderDeliveryParam = orderDeliveryDataParam.getParam();
        String deliveryType = orderDeliveryParam.getDeliveryType();

        //添加包裹
        ShopOrderDelivery shopOrderDelivery = new ShopOrderDelivery();
        shopOrderDelivery.setSiteId(orderData.getSiteId());
        shopOrderDelivery.setOrderId(orderData.getOrderId());
        shopOrderDelivery.setDeliveryType(orderData.getDeliveryType());
        shopOrderDelivery.setSubDeliveryType(deliveryType);
        shopOrderDelivery.setRemark(orderDeliveryParam.getRemark());
        shopOrderDelivery.setLocalDeliverId(orderDeliveryParam.getLocalDeliverId());
        Integer deliveryId = this.packages(shopOrderDelivery);

        for (ShopOrderGoods orderGoods : orderGoodsData)
        {

            orderGoods.setDeliveryStatus(OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus());
            orderGoods.setDeliveryId(deliveryId);
            shopOrderGoodsMapper.updateById(orderGoods);
        }
    }

    @Override
    public void store(OrderDeliveryDataParam orderDeliveryDataParam) {

        ShopOrder orderData = orderDeliveryDataParam.getOrderData();
        List<ShopOrderGoods> orderGoodsData = orderDeliveryDataParam.getOrderGoodsData();
        OrderDeliveryParam orderDeliveryParam = orderDeliveryDataParam.getParam();
        String deliveryType = orderDeliveryParam.getDeliveryType();

        //添加包裹
        ShopOrderDelivery shopOrderDelivery = new ShopOrderDelivery();
        shopOrderDelivery.setSiteId(orderData.getSiteId());
        shopOrderDelivery.setOrderId(orderData.getOrderId());
        shopOrderDelivery.setDeliveryType(orderData.getDeliveryType());
        shopOrderDelivery.setSubDeliveryType(deliveryType);
        shopOrderDelivery.setRemark(orderDeliveryParam.getRemark());
        Integer deliveryId = this.packages(shopOrderDelivery);

        for (ShopOrderGoods orderGoods : orderGoodsData)
        {

            orderGoods.setDeliveryStatus(OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus());
            orderGoods.setDeliveryId(deliveryId);
            shopOrderGoodsMapper.updateById(orderGoods);
        }
    }

    @Override
    public boolean checkFinish(OrderDeliveryParam orderDeliveryParam) {

        Long count = shopOrderGoodsMapper.selectCount(new QueryWrapper<ShopOrderGoods>()
                .eq("order_id", orderDeliveryParam.getOrderId())
                .eq("delivery_status", OrderDeliveryStatusEnum.WAIT_DELIVERY.getStatus())
                .ne("status", OrderGoodsRefundEnum.REFUND_FINISH.getStatus())
            );

        if(count == 0L){
            this.finish(orderDeliveryParam);
        }
        return true;
    }

    @Override
    public boolean finish(OrderDeliveryParam orderDeliveryParam) {

        //查询订单
        ShopOrder order = shopOrderMapper.selectOne(new QueryWrapper<ShopOrder>()
                .eq("order_id", orderDeliveryParam.getOrderId())
                .eq("site_id", orderDeliveryParam.getSiteId())
                .eq("status", OrderStatusEnum.WAIT_DELIVERY.getStatus())
        );
        if (ObjectUtil.isEmpty(order)) throw new CommonException("订单不存在或已发货");
        order.setDeliveryTime(System.currentTimeMillis() / 1000);
        order.setStatus(OrderStatusEnum.WAIT_TAKE.getStatus().toString());
        order.setTimeout(0L);
        shopOrderMapper.updateById(order);

        coreOrderEventService.orderDeliveryAfter(order, orderDeliveryParam);

        // 订单发货后事件
        OrderDeliveryAfterEvent event = new OrderDeliveryAfterEvent();
        event.setSiteId(order.getSiteId());
        event.addAppSign("shop");
        event.setName("OrderDeliveryAfterEvent");
        event.setOrder(order);
        EventAndSubscribeOfPublisher.publishAll(event);

        //如果有且只有一个虚拟商品并且已经收货，则订单完成
        List<ShopOrderGoods> orderGoodsList = shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().eq("order_id", orderDeliveryParam.getOrderId()));
        if(orderGoodsList.size() == 1)
        {
            ShopOrderGoods orderGoods = orderGoodsList.get(0);
            if(orderGoods.getGoodsType().equals(GoodsTypeEnum.VIRTUAL.getType()) && orderGoods.getDeliveryStatus().equals(OrderDeliveryStatusEnum.TAKED.getStatus()))
            {
                OrderFinishParam finishParam = new OrderFinishParam();
                finishParam.setOrderId(orderDeliveryParam.getOrderId());
                finishParam.setSiteId(orderDeliveryParam.getSiteId());
                orderFinishService.finish(finishParam);
            }
        }
        return true;

    }

    @Override
    public String orderShippingUploadShippingInfo(Integer siteId, Integer orderId) {
        return null;
    }

}
