package com.niu.shop.service.core.refund;

import com.niu.shop.service.core.refund.param.ActiveRefundCloseParam;
import com.niu.shop.service.core.refund.param.ActiveRefundParam;

public interface ICoreRefundActionService {

    /**
     * 主动退款
     * @param param
     */
    void shopActiveRefund(ActiveRefundParam param);

    /**
     * 退款关闭
     * @param param
     * @param orderRefundNo
     */
    void close(ActiveRefundCloseParam param, Integer type);
}
