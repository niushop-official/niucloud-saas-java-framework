package com.niu.recharge.listener;

import cn.hutool.json.JSONUtil;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.pay.PaySuccessEvent;
import com.niu.recharge.service.core.ICoreRechargeOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 支付成功
 */
@Slf4j
@EventListen("recharge")
@Component
public class PaySuccessListener extends AbstractListener {

    @Resource
    ICoreRechargeOrderService coreRechargeOrderService;

    public void handleEvent(PaySuccessEvent event) {
        log.info("PaySuccessListener handleEvent: {}", JSONUtil.toJsonStr(event));
        if (event.getTradeType().equals("recharge")) {
            coreRechargeOrderService.pay(event.getPay());
        }
    }
}
