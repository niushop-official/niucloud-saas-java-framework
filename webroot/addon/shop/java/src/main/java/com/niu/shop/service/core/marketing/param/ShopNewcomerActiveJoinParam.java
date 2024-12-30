package com.niu.shop.service.core.marketing.param;

import lombok.Data;

@Data
public class ShopNewcomerActiveJoinParam {
    private Integer siteId;
    private Integer memberId;
    private Integer isJoin;
    private Integer orderId;
}
