package com.niu.shop.service.core.refund;

import com.niu.core.entity.pay.PayRefund;
import com.niu.shop.entity.order.ShopOrderRefund;
import com.niu.shop.service.core.refund.param.AddRefundLogParam;
import com.niu.shop.service.core.refund.param.RefundFinishParam;

public interface ICoreRefundService {

    /**
     * 添加退款记录
     *
     * @param data
     */
    void addLog(AddRefundLogParam data);

    /**
     * 退款完成
     *
     */
    void finish(RefundFinishParam param);

    void refundFinishAfter(ShopOrderRefund refund);

    /**
     * 退款转账完成
     *
     */
    void refundTransferSuccess(PayRefund refund);
}
