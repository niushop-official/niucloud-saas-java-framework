package com.niu.shop.service.admin.refund;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.core.refund.param.ActiveRefundParam;
import com.niu.shop.service.admin.refund.param.AuditApplyParam;
import com.niu.shop.service.admin.refund.param.AuditRefundGoodsParam;
import com.niu.shop.service.admin.refund.param.RefundSearchParam;
import com.niu.shop.service.admin.refund.vo.RefundDetailVo;
import com.niu.shop.service.admin.refund.vo.RefundListVo;

public interface IRefundActionService {

    /**
     * 订单退款列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderRefundListVo>
     */
    PageResult<RefundListVo> page(PageParam pageParam, RefundSearchParam searchParam);

    /**
     * 详情
     *
     * @param refundId
     * @return
     */
    RefundDetailVo detail(Integer refundId);

    /**
     * 订单维权审核
     *
     * @param param
     */
    void auditApply(AuditApplyParam param);

    /**
     * 订单退货审核
     *
     * @param param
     */
    void auditRefundGoods(AuditRefundGoodsParam param);

    /**
     * 商家主动退款
     * @param param
     */
    void shopActiveRefund(ActiveRefundParam param);
}
