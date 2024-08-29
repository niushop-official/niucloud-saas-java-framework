package com.niu.recharge.service.core;

import com.niu.core.entity.pay.Pay;
import com.niu.recharge.entity.RechargeOrder;
import com.niu.recharge.service.core.param.RechargeOrderCreateParam;
import com.niu.recharge.service.core.vo.RechargeOrderCreateVo;

public interface ICoreRechargeOrderService {

    RechargeOrderCreateVo create(RechargeOrderCreateParam param);

    void close(Integer orderId);

    RechargeOrder orderInfo(Integer orderId);

    void pay(Pay pay);
}
