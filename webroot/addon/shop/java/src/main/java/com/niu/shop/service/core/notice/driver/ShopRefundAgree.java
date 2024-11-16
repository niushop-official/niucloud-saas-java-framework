package com.niu.shop.service.core.notice.driver;

import com.niu.core.common.loader.notice.BaseNotice;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderRefund;

import java.util.HashMap;
import java.util.Map;

public class ShopRefundAgree extends BaseNotice {

    public NoticeDataVo noticeData(Map<String, Object> vars) {
        ShopOrderRefund refund = (ShopOrderRefund) vars.get("refund");
        ShopOrder order = (ShopOrder) vars.get("order");

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("order_refund_no", refund.getOrderRefundNo());
        param.put("order_no", order.getOrderNo());
        param.put("refund_money", refund.getApplyMoney());
        param.put("result", "已通过申请");

        NoticeDataVo vo = new NoticeDataVo();
        vo.setMemberId(refund.getMemberId());
        vo.setVars(param);

        return vo;
    }
}
