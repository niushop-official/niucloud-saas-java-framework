package com.niu.recharge.service.admin;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.recharge.service.admin.param.RechargeRefundSearchParam;
import com.niu.recharge.service.admin.vo.*;

public interface IRechargeRefundService {

    /**
     * 退款列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    PageResult<RechargeRefundListVo> list(PageParam pageParam, RechargeRefundSearchParam searchParam);

    /**
     * 发起退款
     */
    void refund(Integer orderId);

    /**
     * 充值退款详情
     *
     * @param refundId
     * @return
     */
    RechargeRefundInfoVo detail(Integer refundId);

    RechargeRefundStatVo stat(RechargeRefundSearchParam searchParam);
}
