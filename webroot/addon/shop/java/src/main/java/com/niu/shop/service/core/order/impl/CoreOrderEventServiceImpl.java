package com.niu.shop.service.core.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.utils.NoticeUtils;
import com.niu.core.service.core.member.ICoreMemberService;
import com.niu.shop.entity.cart.ShopCart;
import com.niu.shop.entity.order.ShopInvoice;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderDiscount;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.entity.order.config.ShopOrderInvoice;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import com.niu.shop.enums.order.*;
import com.niu.shop.mapper.cart.ShopCartMapper;
import com.niu.shop.mapper.order.ShopInvoiceMapper;
import com.niu.shop.mapper.order.ShopOrderDiscountMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.admin.marketing.IShopDiscountService;
import com.niu.shop.service.core.goods.ICoreGoodsService;
import com.niu.shop.service.core.goods.ICoreGoodsStatService;
import com.niu.shop.service.core.goods.param.GoodsAddStatParam;
import com.niu.shop.service.core.marketing.ICoreCouponMemberService;
import com.niu.shop.service.core.marketing.ICoreManjianService;
import com.niu.shop.service.core.order.*;
import com.niu.shop.service.core.order.param.*;
import com.niu.shop.service.core.order.vo.OrderConfigVo;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import com.niu.shop.service.core.order.vo.OrderGoodsVo;
import com.niu.shop.service.core.shop.ICoreStatService;
import com.niu.shop.service.core.shop.param.AddStatParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CoreOrderEventServiceImpl implements ICoreOrderEventService {

    @Resource
    ICoreInvoiceService coreInvoiceService;

    @Resource
    ShopOrderMapper shopOrderMapper;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    ICoreOrderLogService coreOrderLogService;

    @Resource
    ICoreGoodsService coreGoodsService;

    @Resource
    ShopOrderDiscountMapper shopOrderDiscountMapper;

    @Resource
    ICoreCouponMemberService coreCouponMemberService;

    @Resource
    ShopCartMapper cartMapper;

    @Resource
    ICoreOrderConfigService coreOrderConfigService;

    @Resource
    ICoreStatService coreStatService;

    @Resource
    ShopInvoiceMapper shopInvoiceMapper;

    @Resource
    ICoreOrderDeliveryService coreOrderDeliveryService;

    @Resource
    IShopDiscountService shopDiscountService;

    @Resource
    ICoreMemberService coreMemberService;

    @Resource
    ICoreManjianService manjianService;

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    /**
     * 订单关闭之后
     *
     * @param order
     * @param param
     */
    @Override
    @Async
    public void orderCloseAfter(ShopOrder order, OrderCloseParam param) {
        // 发票失效
        if (order.getInvoiceId() > 0) coreInvoiceService.close(order.getInvoiceId());

        List<ShopOrderGoods> orderGoods = shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().eq("order_id", order.getOrderId()));

        for (ShopOrderGoods item : orderGoods) {
            // 扣除销量
            coreGoodsService.decSaleNum(item.getSiteId(), item.getGoodsId(), item.getSkuId(), item.getNum());
            // 返还库存
            coreGoodsService.incStock(item.getSiteId(), item.getGoodsId(), item.getSkuId(), item.getNum());
        }

        // 添加订单日志
        AddOrderLogParam logParam = new AddOrderLogParam();
        logParam.setOrderId(order.getOrderId());
        logParam.setStatus(Integer.valueOf(order.getStatus()));
        logParam.setMainType(ObjectUtil.defaultIfNull(param.getMainType(), OrderLogMainTypeEnum.MEMBER.getType()));
        logParam.setMainId(ObjectUtil.defaultIfNull(param.getMainId(), order.getMemberId()));
        logParam.setType(OrderLogActionEnum.ORDER_CLOSE_ACTION.getAction());
        coreOrderLogService.add(logParam);

        // 返还使用的优惠
        List<ShopOrderDiscount> orderDiscount = shopOrderDiscountMapper.selectList(new QueryWrapper<ShopOrderDiscount>().eq("order_id", order.getOrderId()));
        if (ObjectUtil.isNotEmpty(orderDiscount)) {
            for (ShopOrderDiscount item : orderDiscount) {
                switch (item.getDiscountType()) {
                    case "coupon":
                        coreCouponMemberService.recover(item.getDiscountTypeId());
                        break;
                }
            }
        }
    }

    /**
     * 订单创建之后
     *
     * @param vo
     */
    @Override
    @Async
    public void orderCreateAfter(OrderCreateDataVo vo) {
        log.info("订单AfterShopOrderCreate 订单id:" + vo.getOrderId());
        try {
            // 创建发票
            this.createInvoice(vo);

            for (OrderCreateDataVo.Sku item : vo.getGoodsData().values()) {
                // 增加销量
                coreGoodsService.incSaleNum(item.getSiteId(), item.getGoodsId(), item.getSkuId(), item.getNum());
                // 减少库存
                coreGoodsService.decStock(item.getSiteId(), item.getGoodsId(), item.getSkuId(), item.getNum());

                GoodsAddStatParam goodsAddStatParam = new GoodsAddStatParam();
                goodsAddStatParam.setSiteId(item.getSiteId());
                goodsAddStatParam.setGoodsId(item.getGoodsId());
                goodsAddStatParam.setSaleNum(item.getNum());
                coreGoodsStatService.addStat(goodsAddStatParam);
            }

            //赠品同步增加销量减少库存
            if(ObjectUtil.isNotEmpty(vo.getGiftGoods()))
            {
                for (OrderCreateDataVo.Sku item : vo.getGiftGoods().values()) {
                    // 增加销量
                    coreGoodsService.incSaleNum(item.getSiteId(), item.getGoodsId(), item.getSkuId(), item.getNum());
                    // 减少库存
                    coreGoodsService.decStock(item.getSiteId(), item.getGoodsId(), item.getSkuId(), item.getNum());

                    GoodsAddStatParam goodsAddStatParam = new GoodsAddStatParam();
                    goodsAddStatParam.setSiteId(item.getSiteId());
                    goodsAddStatParam.setGoodsId(item.getGoodsId());
                    goodsAddStatParam.setSaleNum(item.getNum());
                    coreGoodsStatService.addStat(goodsAddStatParam);
                }
            }


            // 清除购物车
            if (ObjectUtil.isNotEmpty(vo.getCartIds())) {
                cartMapper.delete(new QueryWrapper<ShopCart>()
                        .in("id", vo.getCartIds())
                        .eq("site_id", vo.getSiteId())
                        .eq("member_id", vo.getMemberId()));
            }

            // 订单关闭任务
            OrderConfigVo orderConfig = coreOrderConfigService.getConfig(vo.getSiteId());
            if (orderConfig.getCloseOrderInfo().getIsClose() && orderConfig.getCloseOrderInfo().getCloseLength() > 0) {
                ShopOrder order = new ShopOrder();
                order.setOrderId(vo.getOrderId());
                order.setTimeout(System.currentTimeMillis() / 1000 + orderConfig.getCloseOrderInfo().getCloseLength() * 60);
                shopOrderMapper.updateById(order);
            }
            //订单创建成功之后事件，增加满减赠品记录
            manjianService.addGiveRecords(vo);
            // 添加订单日志
            AddOrderLogParam logParam = new AddOrderLogParam();
            logParam.setOrderId(vo.getOrderId());
            logParam.setStatus(OrderStatusEnum.WAIT_PAY.getStatus());
            logParam.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
            logParam.setMainId(vo.getMemberId());
            logParam.setType(OrderLogActionEnum.ORDER_CREATE_ACTION.getAction());
            coreOrderLogService.add(logParam);

            // 添加统计
            AddStatParam statParam = new AddStatParam();
            statParam.setOrderNum(1);
            coreStatService.addStat(vo.getSiteId(), statParam);
        } catch (Exception e) {
            log.error("订单AfterShopOrderCreate失败");
            e.printStackTrace();
        }
    }

    /**
     * 创建发票
     *
     * @param vo
     */
    private void createInvoice(OrderCreateDataVo vo) {
        ShopOrderInvoice config = coreOrderConfigService.invoice(vo.getSiteId());
        if (config.getIsInvoice().equals("1") && ObjectUtil.isNotEmpty(vo.getInvoice().getHeaderName())) {
            ShopInvoice model = new ShopInvoice();
            BeanUtil.copyProperties(vo.getInvoice(), model);
            model.setSiteId(vo.getSiteId());
            model.setMemberId(vo.getMemberId());
            model.setTradeId(vo.getOrderId());
            model.setTradeType(vo.getOrderType());
            model.setMoney(vo.getBasic().getOrderMoney());
            model.setStatus(InvoiceStatusEnum.WAIT_OPEN.getStatus());
            model.setCreateTime(System.currentTimeMillis() / 1000);
            shopInvoiceMapper.insert(model);

            ShopOrder order = new ShopOrder();
            order.setInvoiceId(model.getId());
            order.setOrderId(vo.getOrderId());
            shopOrderMapper.updateById(order);
        }
    }

    /**
     * 订单支付之后
     *
     * @param order
     * @param param
     */
    @Override
    @Async
    public void orderPayAfter(ShopOrder order, OrderPayParam param) {
        // 发票改变状态
        if (order.getInvoiceId() > 0) coreInvoiceService.open(order.getInvoiceId());

        // 添加订单日志
        AddOrderLogParam logParam = new AddOrderLogParam();
        logParam.setOrderId(order.getOrderId());
        logParam.setStatus(Integer.valueOf(order.getStatus()));
        logParam.setMainType(ObjectUtil.defaultIfNull(param.getMainType(), OrderLogMainTypeEnum.MEMBER.getType()));
        logParam.setMainId(ObjectUtil.defaultIfNull(param.getMainId(), order.getMemberId()));
        logParam.setType(OrderLogActionEnum.ORDER_PAY_ACTION.getAction());
        coreOrderLogService.add(logParam);

        // 添加统计
        AddStatParam statParam = new AddStatParam();
        statParam.setSaleMoney(order.getOrderMoney());
        coreStatService.addStat(order.getSiteId(), statParam);

        // 虚拟商品自动发货
        MPJQueryWrapper<ShopOrderGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("og")
                .innerJoin("?_shop_goods g ON g.goods_id = og.goods_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("og.order_goods_id");
        queryWrapper.eq("og.order_id", order.getOrderId());
        queryWrapper.eq("g.goods_type", GoodsTypeEnum.VIRTUAL.getType());
        queryWrapper.eq("g.virtual_auto_delivery", 1);
        List<OrderGoodsVo> orderGoods = shopOrderGoodsMapper.selectJoinList(OrderGoodsVo.class, queryWrapper);
        if (ObjectUtil.isNotEmpty(orderGoods)) {
            OrderDeliveryParam deliveryParam = new OrderDeliveryParam();
            deliveryParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
            deliveryParam.setMainId(0);
            deliveryParam.setSiteId(order.getSiteId());
            deliveryParam.setOrderId(order.getOrderId());
            deliveryParam.setDeliveryType(OrderDeliveryTypeEnum.VIRTUAL.getType());
            deliveryParam.setOrderGoodsIds(orderGoods.stream().map(i -> i.getOrderGoodsId()).toArray(Integer[]::new));
            coreOrderDeliveryService.delivery(deliveryParam);
        }

        // 发送消息
        Map<String, Object> noticeParam = new HashMap<String, Object>();
        noticeParam.put("order", order);
        NoticeUtils.send(order.getSiteId(), "shop_order_pay", noticeParam);

        shopDiscountService.orderPayAfter(order);

        //满减赠送赠品
        manjianService.giftGrant(order);
    }

    /**
     * 订单发货完成之后
     *
     * @param order
     * @param orderDeliveryDataParam
     */
    @Override
    @Async
    public void orderDeliveryAfter(ShopOrder order, OrderDeliveryParam orderDeliveryDataParam) {
        // 订单定时收货任务
        Long timeout = 0L;
        List<ShopOrderGoods> orderGoods = shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().eq("order_id", order.getOrderId()));
        if (orderGoods.size() == 1 && orderGoods.get(0).getIsVerify() == 1) {
            if (orderGoods.get(0).getVerifyExpireTime() > 0) {
                timeout = orderGoods.get(0).getVerifyExpireTime();
            }
        } else {
            OrderConfigVo orderConfig = coreOrderConfigService.getConfig(order.getSiteId());
            if (orderConfig.getConfirm().getIsFinish() && orderConfig.getConfirm().getFinishLength() > 0) {
                timeout = System.currentTimeMillis() / 1000 + orderConfig.getConfirm().getFinishLength() * 60;
            }
        }
        if (timeout > 0) {
            ShopOrder orderUpdate = new ShopOrder();
            orderUpdate.setOrderId(order.getOrderId());
            orderUpdate.setTimeout(timeout);
            shopOrderMapper.updateById(orderUpdate);
        }

        // 添加订单日志
        AddOrderLogParam logParam = new AddOrderLogParam();
        logParam.setOrderId(order.getOrderId());
        logParam.setStatus(OrderStatusEnum.WAIT_TAKE.getStatus());
        logParam.setMainType(ObjectUtil.defaultIfNull(orderDeliveryDataParam.getMainType(), OrderLogMainTypeEnum.SYSTEM.getType()));
        logParam.setMainId(ObjectUtil.defaultIfNull(orderDeliveryDataParam.getMainId(), 0));
        logParam.setType(OrderLogActionEnum.ORDER_DELIVERY_ACTION.getAction());
        coreOrderLogService.add(logParam);

        // 发送消息
        Map<String, Object> noticeParam = new HashMap<String, Object>();
        noticeParam.put("order", order);
        NoticeUtils.send(order.getSiteId(), "shop_order_delivery", noticeParam);
    }


    /**
     * 订单完成之后
     *
     * @param order
     * @return
     */
    @Override
    @Async
    public void orderFinishAfter(ShopOrder order, OrderFinishParam orderFinishParam) {
        // 定时关闭订单允许退款开关
        OrderConfigVo orderConfig = coreOrderConfigService.getConfig(order.getSiteId());
        if (orderConfig.getRefund().getNoAllowRefund()) {
            ShopOrderGoods orderGoodsUpdate = new ShopOrderGoods();
            orderGoodsUpdate.setIsEnableRefund(0);
            shopOrderGoodsMapper.update(orderGoodsUpdate, new QueryWrapper<ShopOrderGoods>().eq("order_id", order.getOrderId()));
        } else {
            if (orderConfig.getRefund().getRefundLength() > 0) {
                ShopOrder orderUpdate = new ShopOrder();
                orderUpdate.setOrderId(order.getOrderId());
                orderUpdate.setTimeout(System.currentTimeMillis() / 1000 + orderConfig.getRefund().getRefundLength() * 60);
                shopOrderMapper.updateById(orderUpdate);
            }
        }

        // 添加订单日志
        AddOrderLogParam logParam = new AddOrderLogParam();
        logParam.setOrderId(order.getOrderId());
        logParam.setStatus(OrderStatusEnum.FINISH.getStatus());
        logParam.setMainType(ObjectUtil.defaultIfNull(orderFinishParam.getMainType(), OrderLogMainTypeEnum.SYSTEM.getType()));
        logParam.setMainId(ObjectUtil.defaultIfNull(orderFinishParam.getMainId(), 0));
        logParam.setType(OrderLogActionEnum.ORDER_FINISH_ACTION.getAction());
        coreOrderLogService.add(logParam);

        // 订单完成发放积分成长值
        Map<String, Object> data = new HashMap<>();
        data.put("related_id", order.getOrderId());
        data.put("from_type", "consume_reward");
        data.put("order_money", order.getOrderMoney());
        coreMemberService.sendGrowth(order.getSiteId(), order.getMemberId(), "shop_buy_goods", data);
        coreMemberService.sendGrowth(order.getSiteId(), order.getMemberId(), "shop_buy_order", data);
        coreMemberService.sendPoint(order.getSiteId(), order.getMemberId(), "shop_buy_goods", data);

    }
}
