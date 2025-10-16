package com.niu.shop.service.core.third_addon.order.param;

import lombok.Data;

@Data
public class CalculateDeliveryMoneyParam {
    private Integer siteId;
    private Integer memberId;
    private Integer provinceId = 0;
    private Integer cityId = 0;
    private Integer districtId = 0;
    private Integer sourceSkuId;
    private Integer num;
}
