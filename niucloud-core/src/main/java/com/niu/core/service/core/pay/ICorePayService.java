package com.niu.core.service.core.pay;

import com.niu.core.common.loader.pay.BasePay;
import com.niu.core.entity.pay.Pay;
import com.niu.core.common.loader.pay.param.PayAsyncNotifyParam;
import com.niu.core.common.loader.pay.param.PayParam;
import com.niu.core.common.loader.pay.param.PayNotifyParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 支付记录服务接口
 */
public interface ICorePayService {

    Object pay(PayParam param);

    BasePay driver(Integer siteId, String channel, String type);

    Pay findPayInfoByTrade(Integer siteId, String tradeType, Integer tradeId);

    Pay findPayInfoByOutTradeNo(Integer siteId, String outTradeNo);

    Object asyncNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response);

    void payNotify(PayNotifyParam param);

    Pay createByTrade(Integer siteId, String tradeType, Integer tradeId);

    String buildNotifyUrl(Integer siteId, String channel, String type, String action);

    void closeByTrade(Integer siteId, String tradeType, Integer tradeId);
}
