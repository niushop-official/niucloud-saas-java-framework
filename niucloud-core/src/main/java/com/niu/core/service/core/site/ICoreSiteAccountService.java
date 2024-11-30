package com.niu.core.service.core.site;

import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.pay.PayRefund;
import com.niu.core.entity.pay.PayTransfer;

public interface ICoreSiteAccountService {

    /**
     * 添加支付账单
     * @param pay
     */
    Integer addPayLog(Pay pay);

    /**
     * 添加退款账单记录
     * @param refund
     */
    Integer addRefundLog(PayRefund refund);

    /**
     * 添加转账账单记录
     * @param transfer
     */
    Integer addTransferLog(PayTransfer transfer);
}
