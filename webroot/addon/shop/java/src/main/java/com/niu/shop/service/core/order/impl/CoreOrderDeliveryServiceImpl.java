package com.niu.shop.service.core.order.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.express.WxMaExpressDelivery;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.*;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingInfoBaseResponse;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.entity.pay.Pay;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.pay.ICorePayChannelService;
import com.niu.core.service.core.weapp.ICoreWeappDeliveryService;
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
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderDeliveryMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.core.delivery.ICoreElectronicSheetService;
import com.niu.shop.service.core.delivery.param.ShopDeliveryPrintElectronicSheetParam;
import com.niu.shop.service.core.delivery.vo.ElectronicSheetPrintVo;
import com.niu.shop.service.core.order.ICoreOrderDeliveryService;
import com.niu.shop.service.core.order.ICoreOrderEventService;
import com.niu.shop.service.core.order.ICoreOrderFinishService;
import com.niu.shop.service.core.order.param.OrderDeliveryDataParam;
import com.niu.shop.service.core.order.param.OrderDeliveryParam;
import com.niu.shop.service.core.order.param.OrderFinishParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
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
    ICoreElectronicSheetService coreElectronicSheetService;

    @Resource
    PayMapper payMapper;

    @Resource
    ICoreWeappDeliveryService coreWeappDeliveryService;

    @Resource
    ICorePayChannelService corePayChannelService;

    @Resource
    public void setOrderFinishService(@Lazy ICoreOrderFinishService orderFinishService) {
        this.orderFinishService = orderFinishService;
    }

    @Resource
    public void setCoreOrderEventService(@Lazy ICoreOrderEventService coreOrderEventService) {
        this.coreOrderEventService = coreOrderEventService;
    }

    @Override
    @Transactional
    public void delivery(OrderDeliveryParam orderDeliveryParam) {

        Integer orderId = orderDeliveryParam.getOrderId();
        Integer siteId = orderDeliveryParam.getSiteId();
        List<Integer> orderGoodsIds = new ArrayList<>(Arrays.asList(orderDeliveryParam.getOrderGoodsIds()));
        ShopOrder shopOrder = shopOrderMapper.selectOne(new QueryWrapper<ShopOrder>()
                .eq("order_id", orderId)
                .eq("site_id", siteId));
        if (ObjectUtil.isEmpty(shopOrder)) throw new CommonException("订单信息不能为空");

        if (!shopOrder.getStatus().equals(OrderStatusEnum.WAIT_DELIVERY.getStatus().toString()) && ObjectUtil.isEmpty(orderDeliveryParam.getDeliveryIds()))
            throw new CommonException("订单状态不是待发货状态");

        // 赠品跟随下单商品一起发货
        List<ShopOrderGoods> giftOrderGoods = shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>()
                .select("order_goods_id")
                .eq("order_id", orderId)
                .eq("is_gift", 1)
                .eq("status", OrderGoodsRefundEnum.NORMAL.getStatus())
                .eq("delivery_status", OrderDeliveryStatusEnum.WAIT_DELIVERY.getStatus())
        );
        if (ObjectUtil.isNotEmpty(giftOrderGoods)) {
            giftOrderGoods.stream().forEach(i -> orderGoodsIds.add(i.getOrderGoodsId()));
        }

        List<ShopOrderGoods> orderGoodsData = shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>()
                .eq("order_id", orderId)
                .in("order_goods_id", orderGoodsIds)
                .eq("status", OrderGoodsRefundEnum.NORMAL.getStatus())
                .eq("delivery_status", ObjectUtil.isNotEmpty(orderDeliveryParam.getDeliveryIds()) ? OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus() : OrderDeliveryStatusEnum.WAIT_DELIVERY.getStatus()));

        if (orderGoodsData.size() != orderGoodsIds.size()) throw new CommonException("订单中存在已退款的商品，不能发货");

        List<String> hasGoodsTypeArray = new ArrayList<>();
        for (ShopOrderGoods orderGoods : orderGoodsData) {
            if (!hasGoodsTypeArray.contains(orderGoods.getGoodsType()) && orderGoods.getIsGift() == 0)
                hasGoodsTypeArray.add(orderGoods.getGoodsType());
        }

        if (hasGoodsTypeArray.size() > 1) throw new CommonException("一次发货只能发送一种商品类型的订单项");
        String goodsType = hasGoodsTypeArray.get(0);

        OrderDeliveryDataParam orderDeliveryDataParam = new OrderDeliveryDataParam();
        orderDeliveryDataParam.setOrderData(shopOrder);
        orderDeliveryDataParam.setOrderGoodsData(orderGoodsData);
        orderDeliveryDataParam.setParam(orderDeliveryParam);

        if (ObjectUtil.isNotEmpty(goodsType)) {
            if (goodsType.equals(GoodsTypeEnum.VIRTUAL.getType())) {
                if (!orderDeliveryParam.getDeliveryType().equals(OrderDeliveryTypeEnum.VIRTUAL.getType()))
                    throw new CommonException("虚拟商品只支持虚拟发货");
                this.virtual(orderDeliveryDataParam);
            } else {
                if (!OrderDeliveryTypeEnum.getMap().containsKey(orderDeliveryParam.getDeliveryType()))
                    throw new CommonException("不支持的配送方式");

                //物流配送
                if (shopOrder.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType())) {
                    this.express(orderDeliveryDataParam);
                }

                //同城配送
                if (shopOrder.getDeliveryType().equals(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
                    this.localDelivery(orderDeliveryDataParam);
                }

                //门店自提
                if (shopOrder.getDeliveryType().equals(OrderDeliveryTypeEnum.STORE.getType())) {
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
        for (ShopOrderGoods orderGoods : orderGoodsData) {

            orderGoods.setDeliveryStatus(OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus());
            orderGoods.setDeliveryId(deliveryId);
            shopOrderGoodsMapper.updateById(orderGoods);
        }

        //调用虚拟配送(生成核销码)
        List<Integer> goodsIds = orderGoodsData.stream().map(ShopOrderGoods::getGoodsId).collect(Collectors.toList());
        List<ShopGoods> tempGoodsList = shopGoodsMapper.selectList(new QueryWrapper<ShopGoods>().in("goods_id", goodsIds));
        for (ShopOrderGoods orderGoods : orderGoodsData) {
            for (ShopGoods shopGoods : tempGoodsList) {
                if (orderGoods.getGoodsId().equals(shopGoods.getGoodsId())) {
                    if (shopGoods.getVirtualReceiveType().equals("verify")) {
                        //生成核销码
                        long expireTime;
                        if (shopGoods.getVirtualVerifyType() == 0) {
                            expireTime = 0L;
                        } else if (shopGoods.getVirtualVerifyType() == 1) {

                            expireTime = System.currentTimeMillis() / 1000 + shopGoods.getVirtualIndate() * 86400;
                        } else {
                            expireTime = (long) shopGoods.getVirtualIndate();
                        }
                        orderGoods.setVerifyExpireTime(expireTime);
                        orderGoods.setIsVerify(1);
                        shopOrderGoodsMapper.updateById(orderGoods);
                    } else if (shopGoods.getVirtualReceiveType().equals("auto")) {
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

    public Integer packages(ShopOrderDelivery shopOrderDelivery, Integer[] deliveryIds) {
        shopOrderDeliveryMapper.update(shopOrderDelivery, new QueryWrapper<ShopOrderDelivery>().in("id", deliveryIds));
        return 0;
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

        if (ObjectUtil.isEmpty(orderDeliveryParam.getDeliveryIds())) {
            switch (orderDeliveryDataParam.getParam().getDeliveryWay()) {
                case "manual_write" -> {
                    if (ObjectUtil.isNotEmpty(orderDeliveryDataParam.getParam().getExpressNumber())) {
                        QueryWrapper<ShopOrderDelivery> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("order_id", orderDeliveryParam.getOrderId())
                            .eq("site_id", orderDeliveryParam.getSiteId())
                            .eq("express_number", orderDeliveryParam.getExpressNumber());
                        if (shopOrderDeliveryMapper.selectCount(queryWrapper) > 0) {
                            throw new RuntimeException("物流单号不能重复");
                        }
                    }
                }
                case "electronic_sheet" -> {
                    if(deliveryType.equals("express"))
                    {
                        ShopDeliveryPrintElectronicSheetParam param = new ShopDeliveryPrintElectronicSheetParam();
                        param.setSiteId(orderDeliveryDataParam.getOrderData().getSiteId());
                        param.setOrderId(orderDeliveryDataParam.getOrderData().getOrderId());
                        param.setElectronicSheetId(orderDeliveryDataParam.getParam().getElectronicSheetId());
                        param.setOrderGoodsIds(CollStreamUtil.toList(orderDeliveryDataParam.getOrderGoodsData(), ShopOrderGoods::getGoodsId));

                        List<ElectronicSheetPrintVo> voList = coreElectronicSheetService.printElectronicSheet(param);
                        if (ObjectUtil.isNotEmpty(voList)) {
                            shopOrderDelivery.setExpressNumber(String.valueOf(voList.get(0).getOrderInfo().get("LogisticCode")));
                        }
                    }

                }
            }
        }

        Integer deliveryId = ObjectUtil.isNotEmpty(orderDeliveryParam.getDeliveryIds()) ? this.packages(shopOrderDelivery, orderDeliveryParam.getDeliveryIds()) : this.packages(shopOrderDelivery);

        if (ObjectUtil.isEmpty(orderDeliveryParam.getDeliveryIds())) {
            for (ShopOrderGoods orderGoods : orderGoodsData) {
                orderGoods.setDeliveryStatus(OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus());
                orderGoods.setDeliveryId(deliveryId);
                shopOrderGoodsMapper.updateById(orderGoods);
            }
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
        Integer deliveryId = ObjectUtil.isNotEmpty(orderDeliveryParam.getDeliveryIds()) ? this.packages(shopOrderDelivery, orderDeliveryParam.getDeliveryIds()) : this.packages(shopOrderDelivery);

        if (ObjectUtil.isEmpty(orderDeliveryParam.getDeliveryIds())) {
            for (ShopOrderGoods orderGoods : orderGoodsData) {
                orderGoods.setDeliveryStatus(OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus());
                orderGoods.setDeliveryId(deliveryId);
                shopOrderGoodsMapper.updateById(orderGoods);
            }
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
        Integer deliveryId = ObjectUtil.isNotEmpty(orderDeliveryParam.getDeliveryIds()) ? this.packages(shopOrderDelivery, orderDeliveryParam.getDeliveryIds()) : this.packages(shopOrderDelivery);

        if (ObjectUtil.isEmpty(orderDeliveryParam.getDeliveryIds())) {
            for (ShopOrderGoods orderGoods : orderGoodsData) {
                orderGoods.setDeliveryStatus(OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus());
                orderGoods.setDeliveryId(deliveryId);
                shopOrderGoodsMapper.updateById(orderGoods);
            }
        }
    }

    @Override
    public boolean checkFinish(OrderDeliveryParam orderDeliveryParam) {

        Long count = shopOrderGoodsMapper.selectCount(new QueryWrapper<ShopOrderGoods>()
                .eq("order_id", orderDeliveryParam.getOrderId())
                .eq("delivery_status", OrderDeliveryStatusEnum.WAIT_DELIVERY.getStatus())
                .ne("status", OrderGoodsRefundEnum.REFUND_FINISH.getStatus())
        );

        Long deliveryCount = shopOrderGoodsMapper.selectCount(new QueryWrapper<ShopOrderGoods>()
                .eq("order_id", orderDeliveryParam.getOrderId())
                .in("delivery_status", Arrays.asList(OrderDeliveryStatusEnum.TAKED.getStatus(), OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus()))
                .ne("status", OrderGoodsRefundEnum.REFUND_FINISH.getStatus())
        );

        if (count == 0L && deliveryCount > 0L) {
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
                .eq("status", ObjectUtil.isNotEmpty(orderDeliveryParam.getDeliveryIds()) ? OrderStatusEnum.WAIT_TAKE.getStatus() : OrderStatusEnum.WAIT_DELIVERY.getStatus())
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
        if (orderGoodsList.size() == 1) {
            ShopOrderGoods orderGoods = orderGoodsList.get(0);
            if (orderGoods.getGoodsType().equals(GoodsTypeEnum.VIRTUAL.getType()) && orderGoods.getDeliveryStatus().equals(OrderDeliveryStatusEnum.TAKED.getStatus())) {
                OrderFinishParam finishParam = new OrderFinishParam();
                finishParam.setOrderId(orderDeliveryParam.getOrderId());
                finishParam.setSiteId(orderDeliveryParam.getSiteId());
                orderFinishService.finish(finishParam);
            }
        }
        return true;

    }

    /**
     * 微信小程序发货入口
     * @param order
     */
    @Override
    public void orderShippingUploadShippingInfo(ShopOrder order) {
        try {
            Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", order.getOutTradeNo()));
            log.info("shopOrder UploadShippingInfo:{}", JSONUtil.toJsonStr(pay));
            if (!pay.getType().equals("wechatpay") || !pay.getChannel().equals("weapp")) return;

            // 检测微信小程序是否已开通发货信息管理服务
            if (!coreWeappDeliveryService.getIsTradeManaged(order.getSiteId()).getIsTradeManaged()) return;

            // 设置消息跳转路径设置接口
            WxMaOrderShippingInfoBaseResponse setResult = coreWeappDeliveryService.setMsgJumpPath(order.getSiteId(), "shop_order");
            if (!setResult.getErrCode().equals(0)) {
                log.info("giftcardOrder UploadShippingInfo setMsgJumpPath", setResult.getErrMsg());
                return;
            }

            JSONObject config = ObjectUtil.defaultIfNull(corePayChannelService.getConfigByChannelAndType(pay.getSiteId(), pay.getChannel(), pay.getType()), new JSONObject());

            WxMaService miniapp = WechatUtils.miniapp(order.getSiteId());

            WxMaOrderShippingInfoUploadRequest request = new WxMaOrderShippingInfoUploadRequest();

            // 需要上传物流信息的订单
            OrderKeyBean orderKey = new OrderKeyBean();
            orderKey.setOrderNumberType(1);
            orderKey.setOutTradeNo(order.getOutTradeNo());
            orderKey.setMchId(config.getStr("mch_id", ""));
            request.setOrderKey(orderKey);

            request.setUploadTime(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

            // 物流模式，发货方式枚举值：1、实体物流配送采用快递公司进行实体物流配送形式 2、同城配送 3、虚拟商品，虚拟商品，例如话费充值，点卡等，无实体配送形式 4、用户自提
            if (order.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType())) {
                request.setLogisticsType(1);
            } else if (order.getDeliveryType().equals(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
                request.setLogisticsType(2);
            } else if (order.getDeliveryType().equals(OrderDeliveryTypeEnum.STORE.getType())) {
                request.setLogisticsType(4);
            } else if (order.getDeliveryType().equals(OrderDeliveryTypeEnum.VIRTUAL.getType())) {
                request.setLogisticsType(3);
            }

            // 发货模式，发货模式枚举值：1、UNIFIED_DELIVERY（统一发货）2、SPLIT_DELIVERY（分拆发货）
            request.setDeliveryMode(1);
            request.setIsAllDelivered(true);

            // 物流信息列表
            List<ShopOrderDelivery> packageList = shopOrderDeliveryMapper.selectJoinList(ShopOrderDelivery.class, new MPJQueryWrapper<ShopOrderDelivery>()
                .setAlias("d").innerJoin("?_shop_delivery_company c ON d.express_company_id = c.company_id".replace("?_", GlobalConfig.tablePrefix))
                .select("express_number,id,express_company_id,c.company_name AS name")
                .eq("order_id", order.getOrderId())
                .eq("d.site_id", order.getSiteId())
            );

            List<WxMaExpressDelivery> expressDelivery = miniapp.getExpressService().getAllDelivery();

            if (packageList.size() > 0) {
                List<ShippingListBean> shippingList = new LinkedList<>();

                for (ShopOrderDelivery shopOrderDelivery : packageList) {
                    ShippingListBean shippingListBean = new ShippingListBean();
                    if (order.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType())) {
                        shippingListBean.setTrackingNo(shopOrderDelivery.getExpressNumber());
                        shippingListBean.setExpressCompany("");
                        expressDelivery.forEach(i -> {
                            if (i.getDeliveryName().equals(shopOrderDelivery.getName())) {
                                shippingListBean.setExpressCompany(i.getDeliveryId());
                            }
                        });
                    }
                    ContactBean contact = new ContactBean();
                    contact.setReceiverContact(order.getTakerMobile().substring(0, 3) + "****" + order.getTakerMobile().substring(7));
                    ShopOrderGoods orderGoods = shopOrderGoodsMapper.selectOne(new QueryWrapper<ShopOrderGoods>().select("GROUP_CONCAT(goods_name SEPARATOR ';') AS goods_name").eq("delivery_id", shopOrderDelivery.getId()).groupBy("delivery_id"));
                    shippingListBean.setItemDesc(orderGoods.getGoodsName());
                    shippingListBean.setContact(contact);

                    shippingList.add(shippingListBean);
                }

                request.setDeliveryMode(2);
                request.setShippingList(shippingList);
            }

            JSONObject json = JSONUtil.parseObj(pay.getJson());

            PayerBean payer = new PayerBean();
            payer.setOpenid(json.getStr("openid", ""));
            request.setPayer(payer);

            WxMaOrderShippingInfoBaseResponse response = miniapp.getWxMaOrderShippingService().upload(request);
            System.out.println("shopOrder uploadShippingInfo response " + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("shopOrder uploadShippingInfo error");
        }
    }

}
