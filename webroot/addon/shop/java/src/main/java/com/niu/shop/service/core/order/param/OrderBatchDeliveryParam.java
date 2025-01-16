package com.niu.shop.service.core.order.param;

import com.niu.shop.service.core.order.model.OrderBatchDeliveryData;
import lombok.Data;

import java.util.List;

@Data
public class OrderBatchDeliveryParam {
    private Integer siteId;
    private Integer mainId;
    private OrderBatchDeliveryData data;
    private List<Integer> orderIds;
}
