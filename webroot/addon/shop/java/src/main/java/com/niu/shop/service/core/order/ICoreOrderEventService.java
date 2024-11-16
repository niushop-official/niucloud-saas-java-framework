package com.niu.shop.service.core.order;

import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.service.core.order.param.*;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;

/**
 * 订单事件
 */
public interface ICoreOrderEventService {

    /**
     * 订单关闭之后
     *
     * @param order
     * @param param
     */
    void orderCloseAfter(ShopOrder order, OrderCloseParam param);

    /**
     * 订单创建之后
     *
     * @param vo
     */
    void orderCreateAfter(OrderCreateDataVo vo);

    /**
     * 订单支付之后
     *
     * @param order
     * @param param
     */
    void orderPayAfter(ShopOrder order, OrderPayParam param);

    /**
     * 订单发货之后
     *
     * @param orderDeliveryDataParam
     * @return
     */
    void orderDeliveryAfter(ShopOrder order, OrderDeliveryParam orderDeliveryDataParam);

    /**
     * 订单完成之后
     *
     * @param order
     */
    void orderFinishAfter(ShopOrder order, OrderFinishParam orderFinishParam);
}
