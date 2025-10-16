package com.niu.core.service.core.pay;

import com.niu.core.common.loader.pay.BasePay;
import com.niu.core.common.loader.pay.param.PayAsyncNotifyParam;
import com.niu.core.common.loader.pay.param.PayNotifyParam;
import com.niu.core.common.loader.pay.param.PayParam;
import com.niu.core.entity.pay.Pay;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.service.core.pay.vo.GetInfoByTradeVo;
import com.niu.core.service.core.pay.vo.PayTypeVo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * 支付记录服务接口
 */
public interface ICorePayService {

    Object pay(PayParam param);

    BasePay driver(Integer siteId, String channel, String type);

    GetInfoByTradeVo getInfoByTrade(Integer siteId, String tradeType, Integer tradeId, String channel, String scene);

    Pay findPayInfoByTrade(Integer siteId, String tradeType, Integer tradeId);

    Pay findPayInfoByOutTradeNo(Integer siteId, String outTradeNo);

    Object asyncNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response);

    void payNotify(PayNotifyParam param);

    Pay createByTrade(Integer siteId, String tradeType, Integer tradeId);

    String buildNotifyUrl(Integer siteId, String channel, String type, String action);

    void closeByTrade(Integer siteId, String tradeType, Integer tradeId);

    List<PayTypeVo> getPayTypeByTrade(Integer siteId, String tradeType, ChannelEnum channel);
    Pay create (Integer siteId, String mainType, Integer mainId, BigDecimal money, String tradeType, Integer tradeId, String body);}
