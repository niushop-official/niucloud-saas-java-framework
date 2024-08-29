package com.niu.recharge.service.admin;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.recharge.service.admin.param.RechargeOrderSearchParam;
import com.niu.recharge.service.admin.vo.RechargeOrderInfoVo;
import com.niu.recharge.service.admin.vo.RechargeOrderListVo;
import com.niu.recharge.service.admin.vo.RechargeOrderStatVo;
import com.niu.recharge.service.core.param.RechargeOrderCreateParam;
import com.niu.recharge.service.core.vo.RechargeOrderCreateVo;

public interface IRechargeOrderService {

    /**
     * 订单列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    PageResult<RechargeOrderListVo> list(PageParam pageParam, RechargeOrderSearchParam searchParam);

    /**
     * 充值订单详情
     *
     * @param orderId
     * @return
     */
    RechargeOrderInfoVo detail(Integer orderId);

    RechargeOrderStatVo stat(RechargeOrderSearchParam searchParam);
}
