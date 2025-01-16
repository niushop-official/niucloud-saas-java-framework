package com.niu.shop.service.core.marketing.param;

import lombok.Data;

@Data
public class GetManjianInfoParam {
    private Integer goodsId;
    private Integer skuId;
    private Integer memberId;
    private Integer siteId;
}
