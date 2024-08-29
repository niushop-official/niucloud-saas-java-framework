package com.niu.recharge.service.core;

public interface ICoreRechargeRefundService {

    /**
     * 发起退款
     *
     * @param orderId
     */
    void create(Integer orderId);

    void refundComplete(String refundNo);
}
