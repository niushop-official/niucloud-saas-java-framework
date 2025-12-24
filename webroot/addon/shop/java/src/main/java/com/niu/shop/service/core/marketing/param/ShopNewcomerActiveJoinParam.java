package com.niu.shop.service.core.marketing.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopNewcomerActiveJoinParam {
    private Integer siteId;
    private Integer memberId;
    private Integer isJoin;
    private Integer orderId;
    private List<Integer> goodsIds;
    private List<Integer> skuIds;
}
