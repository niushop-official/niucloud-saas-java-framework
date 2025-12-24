package com.niu.shop.service.core.goods;

import com.niu.core.entity.pay.PayRefund;
import com.niu.shop.service.core.goods.param.GoodsAddStatParam;
import com.niu.shop.service.core.goods.param.GoodsStatParam;

public interface ICoreGoodsStatService {
    void addStat(GoodsAddStatParam param);

    void saveGoodsPayNumAndMoneyByOrderId(Integer orderId);

    void saveGoodsRefundNumAndMoneyByOrderId(PayRefund refund);

    void decStat(GoodsStatParam param);
}
