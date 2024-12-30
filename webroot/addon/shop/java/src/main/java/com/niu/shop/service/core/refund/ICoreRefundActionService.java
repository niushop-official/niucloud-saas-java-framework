package com.niu.shop.service.core.refund;

import com.niu.shop.service.core.refund.param.ActiveRefundParam;

public interface ICoreRefundActionService {

    /**
     * 主动退款
     * @param param
     */
    void shopActiveRefund(ActiveRefundParam param);
}
