package com.niu.core.service.core.pay.driver;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.loader.pay.BasePay;
import com.niu.core.common.loader.pay.param.PayAsyncNotifyParam;
import com.niu.core.common.loader.pay.param.PayParam;
import com.niu.core.common.loader.pay.param.RefundParam;
import com.niu.core.common.loader.pay.param.TransferParam;
import com.niu.core.entity.pay.Pay;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.service.core.pay.vo.PayTypeVo;
import com.niu.core.service.core.pay.vo.TransferQueryVo;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FriendPay extends BasePay {

    @Override
    public BasePay init(JSONObject config) {
        return this;
    }

    @Override
    public Object pay(PayParam param) {
        return null;
    }

    @Override
    public void scan() {

    }

    @Override
    public void close(Pay pay) {

    }

    @Override
    public Object asyncNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public Object transfer(TransferParam param) {
        return null;
    }

    @Override
    public Object transferCancel(String transferNo) {
        return null;
    }

    @Override
    public TransferQueryVo transferQuery(String transferNo) {
        return null;
    }

    @Override
    public void refund(RefundParam param) {

    }

    @Override
    public PayTypeVo filterPayTypeByTradeType(String tradeType) {
        return JSONUtil.toBean(PayTypeEnum.getType().getJSONObject("friendspay"), PayTypeVo.class);
    }
}
