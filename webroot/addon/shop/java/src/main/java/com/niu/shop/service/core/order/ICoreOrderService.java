package com.niu.shop.service.core.order;

import com.niu.core.entity.pay.Pay;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.service.core.order.param.DeliverySearchParam;
import com.niu.shop.service.core.order.param.OrderCloseParam;
import com.niu.shop.service.core.order.param.OrderPayParam;
import com.niu.shop.service.core.order.vo.DeliveryTraceVo;

import java.util.List;

public interface ICoreOrderService {

    /**
     * 获取订单信息
     *
     * @param orderId
     * @return
     */
    ShopOrder orderInfo(Integer orderId);

    /**
     * 订单关闭
     *
     * @param param
     */
    void close(OrderCloseParam param);

    /**
     * 订单支付成功
     *
     * @param param
     */
    void pay(OrderPayParam param);

    /**
     * 校验是否全部退款
     *
     * @param orderId
     */
    void checkAllClose(Integer orderId);


    /**
     * 查询物流信息
     *
     * @param siteId
     * @param param
     */
    DeliveryTraceVo deliverySearch(Integer siteId, DeliverySearchParam param);

    /**
     * 设置发票ID
     * @param tradeIds
     * @param id
     */
    void setInvoiceIdByOrderId(List<String> tradeIds, Integer id);

    /**
     * 清除发票ID
     * @param id
     */
    void cancelInvoiceId(Integer id);
}
