package com.niu.shop.service.core.order;

import com.niu.shop.service.admin.order.param.ShopOrderAddBatchDeliveryParam;
import com.niu.shop.service.core.order.param.OrderBatchDeliveryParam;
import com.niu.shop.service.core.order.vo.OrderBatchDeliveryVo;

public interface ICoreOrderBatchDeliveryService {
    void add(ShopOrderAddBatchDeliveryParam param);

    void execute(Integer id, Integer siteId);

    OrderBatchDeliveryVo delivery(OrderBatchDeliveryParam param);
}
