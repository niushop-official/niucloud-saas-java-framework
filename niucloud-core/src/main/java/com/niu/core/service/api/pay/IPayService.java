package com.niu.core.service.api.pay;

import com.niu.core.service.api.pay.vo.PayInfoVo;
import com.niu.core.common.loader.pay.param.PayAsyncNotifyParam;
import com.niu.core.common.loader.pay.param.PayParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IPayService {

    Object pay(PayParam param);

    Object asyncNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response);

    PayInfoVo getInfoByTrade(String tradeType, Integer tradeId);
}
