package com.niu.shop.service.core.order;

import com.niu.shop.entity.order.config.ShopOrderEvaluate;
import com.niu.shop.entity.order.config.ShopOrderFinish;
import com.niu.shop.entity.order.config.ShopOrderInvoice;
import com.niu.shop.entity.order.config.ShopOrderRefund;
import com.niu.shop.service.core.order.param.OrderConfigParam;
import com.niu.shop.service.core.order.vo.OrderConfigVo;
import com.niu.shop.service.core.order.vo.ShopOrderCloseVo;

/**
 * 订单配置接口
 */
public interface ICoreOrderConfigService {

    /**
     * 订单设置
     *
     * @param siteId
     * @param orderConfigParam
     */
    void setConfig(Integer siteId, OrderConfigParam orderConfigParam);

    /**
     * 获取订单设置
     *
     * @param siteId
     * @return
     */
    OrderConfigVo getConfig(Integer siteId);

    /**
     * 订单完成配置
     *
     * @param siteId
     * @return
     */
    ShopOrderFinish orderConfirm(Integer siteId);

    /**
     * 订单退款配置
     *
     * @param siteId
     * @return
     */
    ShopOrderRefund orderRefund(Integer siteId);

    /**
     * 订单发票配置
     *
     * @param siteId
     * @return
     */
    ShopOrderInvoice invoice(Integer siteId);

    /**
     * 订单发票配置
     *
     * @param siteId
     * @return
     */
    ShopOrderEvaluate getEvaluateConfig(Integer siteId);

    ShopOrderCloseVo orderClose(Integer siteId);
}
