package com.niu.shop.service.api.refund;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.api.refund.param.ApplyRefundParam;
import com.niu.shop.service.api.refund.param.EditRefundParam;
import com.niu.shop.service.api.refund.param.RefundDeliveryParam;
import com.niu.shop.service.api.refund.param.RefundSearchParam;
import com.niu.shop.service.api.refund.vo.RefundDataVo;
import com.niu.shop.service.api.refund.vo.RefundDetailVo;
import com.niu.shop.service.api.refund.vo.RefundListVo;

public interface IRefundActionService {

    /**
     * 订单退款列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderRefundListVo>
     */
    PageResult<RefundListVo> page(PageParam pageParam, RefundSearchParam searchParam);

    /**
     * 详情
     *
     * @param orderRefundNo
     * @return
     */
    RefundDetailVo detail(String orderRefundNo);

    /**
     * 退款申请
     *
     * @param param
     */
    void apply(ApplyRefundParam param);

    /**
     * 修改退款申请
     *
     * @param param
     */
    void edit(EditRefundParam param);

    /**
     * 买家退货
     *
     * @param param
     */
    void delivery(RefundDeliveryParam param);

    /**
     * 修改买家退货
     *
     * @param param
     */
    void editDelivery(RefundDeliveryParam param);

    /**
     * 查询订单项退款信息
     *
     * @param orderGoodsId
     * @return
     */
    RefundDataVo getRefundData(Integer orderGoodsId);

    /**
     * 查询订单项退款信息
     *
     * @param orderRefundNo
     * @return
     */
    RefundDataVo getRefundData(String orderRefundNo);

    /**
     * 取消退款
     *
     * @param orderRefundNo
     */
    void close(String orderRefundNo);
}
