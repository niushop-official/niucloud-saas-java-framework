package com.niu.shop.service.core.order.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.entity.order.ShopOrderRefund;
import com.niu.shop.enums.order.*;
import com.niu.shop.event.order.OrderCloseAfterEvent;
import com.niu.shop.event.order.OrderPayAfterEvent;
import com.niu.shop.mapper.order.ShopOrderDiscountMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.mapper.order.ShopOrderRefundMapper;
import com.niu.shop.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.shop.service.core.delivery.vo.DeliverySearchConfigVo;
import com.niu.shop.service.core.goods.ICoreGoodsService;
import com.niu.shop.service.core.marketing.ICoreCouponMemberService;
import com.niu.shop.service.core.order.*;
import com.niu.shop.service.core.order.param.*;
import com.niu.shop.service.core.order.vo.DeliveryTraceVo;
import com.niu.shop.service.core.shop.ICoreStatService;
import com.niu.shop.service.core.shop.param.AddStatParam;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class CoreOrderServiceImpl implements ICoreOrderService {

    @Resource
    ShopOrderMapper shopOrderMapper;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    ICorePayService corePayService;

    @Resource
    ShopOrderRefundMapper shopOrderRefundMapper;

    @Resource
    ICoreOrderFinishService coreOrderFinishService;

    @Resource
    ICoreDeliveryConfigService coreDeliveryConfigService;

    @Resource
    ICoreOrderEventService coreOrderEventService;

    @Override
    public ShopOrder orderInfo(Integer orderId) {
        return shopOrderMapper.selectById(orderId);
    }

    /**
     * 订单关闭
     *
     * @param param
     */
    @Override
    @Transactional
    public void close(OrderCloseParam param) {
        QueryWrapper queryWrapper = new QueryWrapper<ShopOrder>().eq("order_id", param.getOrderId()).eq("site_id", param.getSiteId());
        if (param.getMainType().equals(OrderLogMainTypeEnum.MEMBER.getType())) queryWrapper.eq("member_id", param.getMainId());

        ShopOrder order = shopOrderMapper.selectOne(queryWrapper);
        if (order == null) throw new CommonException("订单不存在");
        if (order.getStatus().equals(OrderStatusEnum.CLOSE.getStatus().toString())) throw new CommonException("订单已关闭");
        if (param.getCloseType().equals(OrderCloseTypeEnum.AUTO_CLOSE.getType()) && !order.getStatus().equals(OrderStatusEnum.WAIT_PAY.getStatus().toString())) return;

        order.setStatus(OrderStatusEnum.CLOSE.getStatus().toString());
        order.setCloseType(param.getCloseType());
        order.setCloseRemark(param.getCloseRemark());
        order.setIsEnableRefund(0);
        order.setTimeout(0L);
        order.setCloseTime(System.currentTimeMillis() / 1000);
        shopOrderMapper.updateById(order);

        if (!param.getCloseType().equals(OrderCloseTypeEnum.REFUND_CLOSE.getType())) {
            corePayService.closeByTrade(order.getSiteId(), order.getOrderType(), order.getOrderId());
        }

        order = shopOrderMapper.selectById(order.getOrderId());

        // 订单关闭之后事件
        OrderCloseAfterEvent event = new OrderCloseAfterEvent();
        event.setSiteId(order.getSiteId());
        event.addAppSign("shop");
        event.setName("OrderCloseAfterEvent");
        event.setOrder(order);
        EventAndSubscribeOfPublisher.publishAll(event);

        coreOrderEventService.orderCloseAfter(order, param);
    }

    /**
     * 订单支付成功
     *
     * @param param
     */
    @Transactional
    public void pay(OrderPayParam param) {
        ShopOrder order = shopOrderMapper.selectOne(new QueryWrapper<ShopOrder>().select("order_id,status").eq("order_id", param.getTradeId()));
        if (order == null) throw new CommonException("订单不存在");
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_PAY.getStatus().toString())) throw new CommonException("订单状态异常");

        // 变更订单状态
        order.setStatus(OrderStatusEnum.WAIT_DELIVERY.getStatus().toString());
        order.setTimeout(0L);
        order.setIsEnableRefund(1);
        order.setOutTradeNo(param.getOutTradeNo());
        order.setPayTime(System.currentTimeMillis() / 1000);
        shopOrderMapper.updateById(order);

        // 变更订单项发货状态
        ShopOrderGoods orderGoods = new ShopOrderGoods();
        orderGoods.setDeliveryStatus(OrderDeliveryStatusEnum.WAIT_DELIVERY.getStatus());
        orderGoods.setIsEnableRefund(1);
        shopOrderGoodsMapper.update(orderGoods, new QueryWrapper<ShopOrderGoods>().eq("order_id", order.getOrderId()));

        order = shopOrderMapper.selectById(order.getOrderId());


        ShopOrder finalOrder = order;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                coreOrderEventService.orderPayAfter(finalOrder, param);
            }
        });
        // 订单支付之后事件
        OrderPayAfterEvent event = new OrderPayAfterEvent();
        event.setSiteId(order.getSiteId());
        event.addAppSign("shop");
        event.setName("OrderPayAfterEvent");
        event.setOrder(order);
        EventAndSubscribeOfPublisher.publishAll(event);

    }

    /**
     * 校验是否全部退款
     *
     * @param orderId
     */
    @Override
    public void checkAllClose(Integer orderId) {
        Long count = shopOrderGoodsMapper.selectCount(new QueryWrapper<ShopOrderGoods>()
                .eq("order_id", orderId)
                .ne("status", OrderGoodsRefundEnum.REFUND_FINISH.getStatus())
        );
        if (count == 0) {
            ShopOrder order = shopOrderMapper.selectOne(new QueryWrapper<ShopOrder>().select("order_id,site_id,order_money").eq("order_id", orderId));
            ShopOrderRefund refund = shopOrderRefundMapper.selectOne(new QueryWrapper<ShopOrderRefund>()
                    .select("SUM(money) AS money")
                    .eq("order_id", orderId)
                    .eq("status", OrderRefundStatusEnum.FINISH.getStatus()));

            if (refund.getMoney().compareTo(order.getOrderMoney()) != -1) {
                OrderCloseParam closeParam = new OrderCloseParam();
                closeParam.setOrderId(order.getOrderId());
                closeParam.setSiteId(order.getSiteId());
                closeParam.setCloseType(OrderCloseTypeEnum.REFUND_CLOSE.getType());
                closeParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
                closeParam.setMainId(0);
                this.close(closeParam);
            } else {
                OrderFinishParam finishParam = new OrderFinishParam();
                finishParam.setOrderId(order.getOrderId());
                finishParam.setSiteId(order.getSiteId());
                coreOrderFinishService.finish(finishParam);
            }
        }
    }

    /**
     *
     * 查询物流信息
     * @param siteId
     * @param param
     */
    @Override
    public DeliveryTraceVo deliverySearch(Integer siteId, DeliverySearchParam param) {
        DeliverySearchConfigVo config = coreDeliveryConfigService.getDeliverySearchConfig(siteId);

        // TODO:: 暂时先固定查询快递鸟接口 后期考虑扩展
        Map<String, String> RequestData = new HashMap<>();
        RequestData.put("ShipperCode", param.getExpressNo());
        RequestData.put("LogisticCode", param.getLogisticNo());
        RequestData.put("CustomerName", param.getMobile().substring(param.getMobile().length() - 4));

        Map<String, String> data = new HashMap<>();
        data.put("EBusinessID", config.getKdniaoId());
        data.put("RequestType", config.getKdniaoIsPay().equals(2) ? "8001" : "1002");
        data.put("RequestData", JSONUtil.parseObj(RequestData).toString());
        data.put("DataType", "2");
        data.put("DataSign", Base64.encode(SecureUtil.md5((String) data.get("RequestData") + config.getKdniaoAppKey()).getBytes()));

        String result = HttpRequest
                .post("https://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx")
                .formStr(data)
                .execute()
                .body();

        DeliveryTraceVo vo = new DeliveryTraceVo();
        if (!JSONUtil.isJson(result)) return vo;

        JSONObject resultData = JSONUtil.parseObj(result);

        if (resultData.getBool("Success", false)) {
            vo.setSuccess(true);
            List<DeliveryTraceVo.Trace> traceList = new LinkedList<>();
            JSONArray traces = resultData.getJSONArray("Traces");
            for (int i = traces.size() - 1; i >= 0; i--) {
                JSONObject trace = traces.getJSONObject(i);
                DeliveryTraceVo.Trace traceVo = new DeliveryTraceVo.Trace();
                traceVo.setDatetime(trace.getStr("AcceptTime", ""));
                traceVo.setRemark(trace.getStr("AcceptStation", ""));
                traceList.add(traceVo);
            }
            vo.setList(traceList);
        } else {
            vo.setReason(resultData.getStr("Reason", "物流跟踪配置错误"));
        }

        return vo;
    }
}
