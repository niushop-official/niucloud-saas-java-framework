package com.niu.shop.service.core.order;

import com.niu.shop.entity.order.ShopOrderLog;
import com.niu.shop.service.core.order.param.AddOrderLogParam;

import java.util.List;

public interface ICoreOrderLogService {

    /**
     * 添加订单日志
     *
     * @param data
     */
    void add(AddOrderLogParam data);


    void addBatch(List<ShopOrderLog> dataList);
}
