package com.niu.shop.service.core.order;

import com.niu.shop.service.core.order.param.AddOrderLogParam;

public interface ICoreOrderLogService {

    /**
     * 添加订单日志
     *
     * @param data
     */
    void add(AddOrderLogParam data);
}
