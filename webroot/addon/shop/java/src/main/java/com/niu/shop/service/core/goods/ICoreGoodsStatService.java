package com.niu.shop.service.core.goods;

import com.niu.core.entity.pay.PayRefund;
import com.niu.shop.service.core.goods.param.GoodsAddStatParam;

public interface ICoreGoodsStatService {
    void addStat(GoodsAddStatParam param);

    void saveGoodsPayNumAndMoneyByOrderId(Integer orderId);

    void saveGoodsRefundNumAndMoneyByOrderId(PayRefund refund);
}
