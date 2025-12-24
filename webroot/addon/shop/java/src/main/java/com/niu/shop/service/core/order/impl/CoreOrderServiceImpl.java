package com.niu.shop.service.core.order.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.shop.entity.delivery.ShopDeliveryCompany;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.entity.order.ShopOrderRefund;
import com.niu.shop.enums.order.*;
import com.niu.shop.event.order.OrderCloseAfterEvent;
import com.niu.shop.event.order.OrderPayAfterEvent;
import com.niu.shop.mapper.delivery.ShopDeliveryCompanyMapper;
import com.niu.shop.mapper.order.ShopOrderDiscountMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.mapper.order.ShopOrderRefundMapper;
import com.niu.shop.service.api.order.vo.DeliveryPackageVo;
import com.niu.shop.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.shop.service.core.delivery.vo.DeliverySearchConfigVo;
import com.niu.shop.service.core.goods.ICoreGoodsService;
import com.niu.shop.service.core.marketing.ICoreCouponMemberService;
import com.niu.shop.service.core.order.*;
import com.niu.shop.service.core.order.param.*;
import com.niu.shop.service.core.order.vo.DeliveryTraceVo;
import com.niu.shop.service.core.shop.ICoreStatService;
import com.niu.shop.service.core.shop.param.AddStatParam;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import jakarta.annotation.Resource;

import java.io.IOException;
import java.util.*;

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

    @Resource
    MemberMapper memberMapper;

    @Resource
    ShopDeliveryCompanyMapper shopDeliveryCompanyMapper;

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
        //更新会员最后下单时间
        if(order.getMemberId() != 0)
        {
            Member updateMember = new Member();
            updateMember.setLastConsumTime(System.currentTimeMillis() / 1000);
            memberMapper.update(updateMember, new QueryWrapper<Member>().eq("member_id", order.getMemberId()));

        }
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

        // 判断调用的是哪个快递，暂时现在有快递鸟和快递100俩种类型（快递鸟==1，快递100==2）
        if(config.getInterfaceType()==1){
            // 快递鸟调用
            return this.orderTracesKuaidiniao(config,param);
        } else if (config.getInterfaceType() == 2) {
            // 快递100调用
            return this.orderTraces100(config, param);
        } else {
            //没有其他的配置返回空值
            return new DeliveryTraceVo();
        }
    }

    @Override
    public void setInvoiceIdByOrderId(List<String> tradeIds, Integer id) {
        shopOrderMapper.update(new LambdaUpdateWrapper<ShopOrder>()
                .in(ShopOrder::getOrderId, tradeIds)
                .set(ShopOrder::getInvoiceId, id));

    }

    @Override
    public void cancelInvoiceId(Integer id) {
        shopOrderMapper.update(new LambdaUpdateWrapper<ShopOrder>()
                .eq(ShopOrder::getInvoiceId, id)
                .set(ShopOrder::getInvoiceId, 0));
    }

    /**
     * 快递鸟查询物流信息
     * @param config
     * @param param
     * @return
     */
    public DeliveryTraceVo orderTracesKuaidiniao(DeliverySearchConfigVo config, DeliverySearchParam param) {
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
                .post("https://api.kdniao.com/api/dist")
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


    /**
     * 快递100查询物流信息
     * @param config
     * @param deliverySearchParam
     * @return
     */
    public DeliveryTraceVo orderTraces100(DeliverySearchConfigVo config, DeliverySearchParam deliverySearchParam) {

        if(ObjectUtil.isNotEmpty(deliverySearchParam.getCompanyId())){
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("company_id", deliverySearchParam.getCompanyId());
            queryWrapper.select("kd100_express_no");
            ShopDeliveryCompany shopDeliveryCompany = shopDeliveryCompanyMapper.selectOne(queryWrapper);
            deliverySearchParam.setExpressNo(shopDeliveryCompany.getKd100ExpressNo());
        }

        // 构建请求参数
        Map<String, Object> param = new HashMap<>();
        param.put("com", deliverySearchParam.getExpressNo());  // 快递公司编码
        param.put("num", deliverySearchParam.getLogisticNo()); // 快递单号

        if (deliverySearchParam.getMobile() != null && !deliverySearchParam.getMobile().isEmpty()) {
            param.put("phone", deliverySearchParam.getMobile()); // 手机号
        }

        // 构建POST数据
        Map<String, String> postData = new HashMap<>();
        postData.put("customer", config.getKd100Customer().toString());
        try {
            // 将参数转换为JSON字符串
            String paramJson = JSONUtil.toJsonStr(param);
            postData.put("param", paramJson);

            // 生成签名
            String sign = SecureUtil.md5(paramJson+config.getKd100AppKey()+config.getKd100Customer().toString());
            postData.put("sign", sign.toUpperCase());

            // 发送POST请求
            String result = HttpRequest
                    .post("https://poll.kuaidi100.com/poll/query.do")
                    .formStr(postData)
                    .execute().body();

            DeliveryTraceVo vo = new DeliveryTraceVo();
            // 解析返回结果 处理返回结果
            if (result != null) {
                JSONObject resultData = JSONUtil.parseObj(result);
                if (Boolean.FALSE.equals(resultData.get("result"))) {
                    vo.setSuccess(false);
                    vo.setReason((String) resultData.get("message"));
                }

                if (resultData.containsKey("status")) {
                    int status = resultData.getInt("status");
                    if (status != 200) {
                        vo.setSuccess(false);
                        vo.setReason((String) resultData.get("message"));
                    } else if (status == 200) {
                        List<DeliveryTraceVo.Trace> list = new ArrayList<>();
                        if (resultData.containsKey("data")) {
                            List<Map<String, String>> data = (List<Map<String, String>>) resultData.get("data");
                            for (Map<String, String> trace : data) {
                                DeliveryTraceVo.Trace item = new DeliveryTraceVo.Trace();
                                item.setDatetime(trace.get("time"));
                                item.setRemark(trace.get("context"));
                                list.add(item);
                            }
                            Collections.reverse(list); // 反转列表，最新的物流信息在前
                        }

                        vo.setSuccess(true);
                        vo.setStatus(status);
                        vo.setList(list);
                    }
                }
            }else{
                vo.setSuccess(false);
                vo.setReason("物流查询失败，请检查快递100配置");
            }
            return vo;
        } catch (Exception e) {
            throw new AdminException("物流查询失败，请检查快递100配置");
        }
    }

}
