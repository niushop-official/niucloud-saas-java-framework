package com.niu.shop.service.admin.order.param;

import com.niu.shop.enums.order.OrderBatchDeliveryTypeEnum;
import com.niu.shop.service.core.order.model.OrderBatchDeliveryData;
import lombok.Data;

@Data
public class ShopOrderAddBatchDeliveryParam {
    private OrderBatchDeliveryData data;
    private Integer siteId;
    private OrderBatchDeliveryTypeEnum type;
    private Integer mainId;
}
