package com.niu.shop.service.core.order;

import com.niu.shop.service.core.order.param.OrderFinishParam;

public interface ICoreOrderFinishService {

    /**
     * 订单完成
     * @param param
     * @return
     */
    boolean finish(OrderFinishParam param);

    boolean orderGoodsTake(Integer orderId, Integer[] orderGoodsIds);

    void orderShippingNotifyConfirmReceive(Integer siteId, String outTradeNo, String deliveryType);
}