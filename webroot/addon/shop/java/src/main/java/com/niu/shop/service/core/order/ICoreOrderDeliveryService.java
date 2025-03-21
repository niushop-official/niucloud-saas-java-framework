package com.niu.shop.service.core.order;

import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderDelivery;
import com.niu.shop.service.core.order.param.OrderDeliveryDataParam;
import com.niu.shop.service.core.order.param.OrderDeliveryParam;

/**
 * 订单发货接口
 */
public interface ICoreOrderDeliveryService {

    /**
     * 订单发货
     */
    void delivery(OrderDeliveryParam orderDeliveryParam);

    /**
     * 虚拟发货
     */
    void virtual(OrderDeliveryDataParam orderDeliveryDataParam);

    /**
     * 包裹
     */
    Integer packages(ShopOrderDelivery shopOrderDelivery);

    /**
     * 物流包裹
     */
    void express(OrderDeliveryDataParam orderDeliveryDataParam);

    /**
     * 同城配送
     */
    void localDelivery(OrderDeliveryDataParam orderDeliveryDataParam);

    /**
     * 门店自提
     */
    void store(OrderDeliveryDataParam orderDeliveryDataParam);

    /**
     * 检测订单发货完成
     * @return
     */
    boolean checkFinish(OrderDeliveryParam orderDeliveryParam);

    /**
     * 订单发货完成
     * @return
     */
    boolean finish(OrderDeliveryParam orderDeliveryParam);

    /**
     * 微信小程序发货入口
     * @return
     */
    void orderShippingUploadShippingInfo(ShopOrder order);
}
