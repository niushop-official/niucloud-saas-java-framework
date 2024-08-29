package com.niu.core.service.core.pay;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.loader.pay.param.RefundNotifyParam;
import com.niu.core.entity.pay.PayRefund;
import com.niu.core.service.core.pay.param.PayRefundCreateParam;
import com.niu.core.service.core.pay.param.PayRefundTransferParam;
import com.niu.core.service.core.pay.param.PayTransferParam;
import com.niu.core.service.core.pay.param.PayTransferSearchParam;
import com.niu.core.service.core.pay.vo.PayTransferInfoVo;
import com.niu.core.service.core.pay.vo.PayTransferListVo;

/**
 * 转账服务接口
 */
public interface ICoreRefundService {

    /**
     * 创建退款单据
     *
     * @param param
     * @return
     */
    String create(PayRefundCreateParam param);

    /**
     * 退款
     *
     */
    void refund(PayRefundTransferParam param);

    /**
     * 通过交易流水号查询退款单据
     *
     * @param siteId
     * @param refundNo
     * @return
     */
    PayRefund findPayInfoByOutTradeNo(Integer siteId, String refundNo);

    /**
     * 退款结果通知
     * @param param
     */
    void refundNotify(RefundNotifyParam param);
}
