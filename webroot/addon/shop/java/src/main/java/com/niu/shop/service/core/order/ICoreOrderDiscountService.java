package com.niu.shop.service.core.order;

import com.niu.shop.entity.order.ShopOrderDiscount;

import java.util.List;

public interface ICoreOrderDiscountService {

    /**
     * 添加订单优惠
     *
     * @param list
     */
    void inserAll(List<ShopOrderDiscount> list);
}
