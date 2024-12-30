package com.niu.shop.service.core.refund;

import com.niu.core.entity.pay.PayRefund;
import com.niu.shop.entity.order.ShopOrderRefund;
import com.niu.shop.service.core.refund.param.AddRefundLogParam;
import com.niu.shop.service.core.refund.param.RefundFinishParam;
import com.niu.shop.service.core.refund.vo.OrderRefundMoneyVo;

import java.math.BigDecimal;

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
     * 转账
     * @param refund
     */
    void toTransfer(ShopOrderRefund refund);

    /**
     * 退款转账完成
     *
     */
    void refundTransferSuccess(PayRefund refund);

    /**
     * 退款校验
     * @param orderGoodsIds
     * @param applyRefundMoney
     */
    OrderRefundMoneyVo refundCheck(Integer[] orderGoodsIds, BigDecimal applyRefundMoney);
}
