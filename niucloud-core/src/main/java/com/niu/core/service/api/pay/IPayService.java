package com.niu.core.service.api.pay;

import com.niu.core.common.loader.pay.param.PayAsyncNotifyParam;
import com.niu.core.common.loader.pay.param.PayParam;
import com.niu.core.service.api.pay.vo.FriendspayInfoVo;
import com.niu.core.service.core.pay.vo.GetInfoByTradeVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IPayService {

    Object pay(PayParam param);

    Object asyncNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response);

    GetInfoByTradeVo getInfoByTrade(String tradeType, Integer tradeId, String scene);

    FriendspayInfoVo friendspayInfo(String tradeType, Integer tradeId);
}
