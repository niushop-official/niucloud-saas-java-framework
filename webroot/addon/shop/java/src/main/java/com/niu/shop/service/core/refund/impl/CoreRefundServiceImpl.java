package com.niu.shop.service.core.refund.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.pay.PayRefund;
import com.niu.core.enums.pay.RefundTypeEnum;
import com.niu.core.event.common.CommonEvent;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.pay.param.PayRefundCreateParam;
import com.niu.core.service.core.pay.param.PayRefundTransferParam;
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
import com.niu.shop.service.core.marketing.ICoreManjianService;
import com.niu.shop.service.core.order.ICoreOrderDeliveryService;
import com.niu.shop.service.core.order.ICoreOrderFinishService;
import com.niu.shop.service.core.order.ICoreOrderService;
import com.niu.shop.service.core.order.param.OrderDeliveryParam;
import com.niu.shop.service.core.refund.ICoreRefundService;
import com.niu.shop.service.core.refund.param.AddRefundLogParam;
import com.niu.shop.service.core.refund.param.RefundFinishParam;
import com.niu.shop.service.core.refund.vo.OrderRefundMoneyVo;
import com.niu.shop.service.core.shop.ICoreStatService;
import com.niu.shop.service.core.shop.param.AddStatParam;
import org.checkerframework.checker.units.qual.A;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

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
    ICoreManjianService coreManjianService;

    @Resource
    com.niu.core.service.core.pay.ICoreRefundService corePayRefundService;

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

        coreRefundService.refundFinishAfter(refund);
        OrderRefundFinishAfterEvent event = new OrderRefundFinishAfterEvent();
        event.setSiteId(refund.getSiteId());
        event.addAppSign("shop");
        event.setName("OrderRefundFinishAfterEvent");
        event.setRefund(refund);
        EventAndSubscribeOfPublisher.publishAll(event);


    }

    @Async
    @Override
    public void refundFinishAfter(ShopOrderRefund refund) {
        ShopOrder order = shopOrderMapper.selectById(refund.getOrderId());
        if (order == null) return;
        //退还满减赠送
        coreManjianService.refundGift(refund);
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

        //通知三方应用更新订单状态
        if (StringUtils.isNotEmpty(order.getRelateSource())) {
            CommonEvent sendEvent = new CommonEvent();
            Map<String, Object> data = new HashMap<>();
            data.put("source_order_id", refund.getOrderId());
            data.put("source_order_item_id", refund.getOrderGoodsId());
            data.put("relate_source", order.getRelateSource());
            data.put("relate_order_id", order.getRelateOrderId());
            data.put("refund_type", refund.getRefundType());
            data.put("reason", refund.getReason());
            data.put("apply_time", refund.getCreateTime());
            data.put("apply_money", refund.getApplyMoney());
            data.put("is_refund_third_cost", refund.getIsRefundDelivery());
            data.put("money", refund.getMoney());
            data.put("refund_no", refund.getRefundNo());
            data.put("order_refund_no", refund.getOrderRefundNo());
            sendEvent.setSource("shop");
            sendEvent.setEventName("ThirdOrderRefundFinishNotice");
            sendEvent.setData(data);
            sendEvent.setSiteId(refund.getSiteId());
            EventAndSubscribeOfPublisher.publishAll(sendEvent);
        }
    }


    /**
     * 转账
     * @param refund
     */
    @Override
    @Async
    public void toTransfer(ShopOrderRefund refund) {
        try {
            if (refund.getMoney().compareTo(BigDecimal.ZERO) == 0) {
                RefundFinishParam param = new RefundFinishParam();
                param.setOrderRefundNo(refund.getOrderRefundNo());
                this.finish(param);
            } else {
                ShopOrder order = shopOrderMapper.selectOne(new QueryWrapper<ShopOrder>()
                        .select("order_id,order_type,out_trade_no")
                        .eq("order_id", refund.getOrderId()));
                if (order == null) throw new CommonException("订单已失效");

                // 创建退款单据
                PayRefundCreateParam param = new PayRefundCreateParam();
                param.setSiteId(refund.getSiteId());
                param.setMoney(refund.getMoney());
                param.setTradeId(order.getOrderId().toString());
                param.setTradeType(order.getOrderType());
                param.setOutTradeNo(order.getOutTradeNo());
                param.setReason("买家申请退款");
                String refunNo = corePayRefundService.create(param);

                ShopOrderRefund model = new ShopOrderRefund();
                model.setRefundId(refund.getRefundId());
                model.setRefundNo(refunNo);
                shopOrderRefundMapper.updateById(model);

                // 发起退款
                PayRefundTransferParam refundParam = new PayRefundTransferParam();
                refundParam.setRefundNo(refunNo);
                refundParam.setSiteId(refund.getSiteId());
                refundParam.setRefundType(RefundTypeEnum.BACK.getKey());
                refundParam.setVoucher("");
                corePayRefundService.refund(refundParam);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("退款转账失败");
        }
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

    /**
     * 退款校验
     * @param orderGoodsIds
     * @param applyRefundMoney
     */
    @Override
    public OrderRefundMoneyVo refundCheck(Integer[] orderGoodsIds, BigDecimal applyRefundMoney) {
//        if (applyRefundMoney.compareTo(BigDecimal.ZERO) <= 0) throw new CommonException("退款金额不能小于0");

        OrderRefundMoneyVo vo = this.getOrderRefundMoney(orderGoodsIds);
//        if (applyRefundMoney.compareTo(vo.getRefundMoney()) > 0) throw new CommonException("退款金额不能大于可退款总额");

        return vo;
    }

    /**
     * 获取订单最大可退金额
     * @param orderGoodsIds
     * @return
     */
    private OrderRefundMoneyVo getOrderRefundMoney(Integer[] orderGoodsIds) {
        BigDecimal refundMoney = new BigDecimal(0);

        List<OrderRefundMoneyVo.ShopOrderGoods> orderGoodsList = CollectUtils.convert(shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().in("order_goods_id", orderGoodsIds)), OrderRefundMoneyVo.ShopOrderGoods.class);
        if (ObjectUtil.isEmpty(orderGoodsList)) throw new CommonException("订单已失效");

        ShopOrder order = shopOrderMapper.selectOne(new QueryWrapper<ShopOrder>().eq("order_id", orderGoodsList.get(0).getOrderId()));
        if (ObjectUtil.isEmpty(order)) throw new CommonException("订单已失效");

        String[] statusList = new String[]{ OrderStatusEnum.WAIT_DELIVERY.getStatus().toString(), OrderStatusEnum.WAIT_TAKE.getStatus().toString(), OrderStatusEnum.FINISH.getStatus().toString() };
        if (!Arrays.asList(statusList).contains(order.getStatus())) {
            throw new CommonException("退款已失效(退款未支付或已关闭)");
        }

        // 查询是否是最后一笔退款且还没有退运费
        Long orderGoodsCount = shopOrderGoodsMapper.selectCount(new QueryWrapper<ShopOrderGoods>()
                .eq("order_id", order.getOrderId())
                .eq("is_gift", 0)
        );
        Long refundCount = shopOrderRefundMapper.selectCount(new QueryWrapper<ShopOrderRefund>()
                .eq("order_id", order.getOrderId())
                .ne("status", OrderRefundStatusEnum.CLOSE.getStatus())
        );
        Boolean isRefundDelivery = false;
        if (refundCount + orderGoodsIds.length >= orderGoodsCount) {
            Long refundDeliveryCount = shopOrderRefundMapper.selectCount(new QueryWrapper<ShopOrderRefund>()
                    .eq("order_id", order.getOrderId())
                    .eq("is_refund_delivery", 1)
                    .ne("status", OrderRefundStatusEnum.CLOSE.getStatus())
            );
            if (refundDeliveryCount == 0) isRefundDelivery = true;
        }

        for (int i = 0; i < orderGoodsList.size(); i++) {
            OrderRefundMoneyVo.ShopOrderGoods orderGoods = orderGoodsList.get(i);
            if (orderGoods.getIsEnableRefund().equals(0)) throw new CommonException("订单不允许退款");
            if (!orderGoods.getStatus().equals(OrderGoodsRefundEnum.NORMAL.getStatus())) throw new CommonException("订单已退款或存在未完成的退款");

            BigDecimal itemRefundMoney = orderGoods.getGoodsMoney().subtract(orderGoods.getDiscountMoney()).subtract(orderGoods.getShopActiveRefundMoney());

            if (isRefundDelivery && i == 0) {
                itemRefundMoney = itemRefundMoney.add(order.getDeliveryMoney());
                orderGoods.setIsRefundDelivery(1);
            } else {
                orderGoods.setIsRefundDelivery(0);
            }
            orderGoods.setItemRefundMoney(itemRefundMoney);
            refundMoney = refundMoney.add(itemRefundMoney);
        }

        OrderRefundMoneyVo vo = new OrderRefundMoneyVo();
        vo.setRefundMoney(refundMoney);
        vo.setOrderGoodsList(orderGoodsList);
        return vo;
    }
}
