package com.niu.core.service.admin.pay;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.pay.param.PayRefundSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.core.pay.param.PayRefundTransferParam;
import com.niu.core.service.admin.pay.vo.PayRefundInfoVo;
import com.niu.core.service.admin.pay.vo.PayRefundListVo;

/**
 * 退款记录服务接口
 */
public interface IPayRefundService {


    /**
     * 退款记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<PayRefundListVo>
     */
    PageResult<PayRefundListVo> list(PageParam pageParam, PayRefundSearchParam searchParam);

    /**
     * 退款记录详情
     * @param refundNo
     * @return PayRefundInfoVo
     */
    PayRefundInfoVo info(String refundNo);

    /**
     *
     *
     * @param param
     */
    void transfer(PayRefundTransferParam param);
}
