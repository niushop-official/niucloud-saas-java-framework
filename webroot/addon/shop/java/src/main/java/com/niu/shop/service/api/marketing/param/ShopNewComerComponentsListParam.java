package com.niu.shop.service.api.marketing.param;

import lombok.Data;

import java.util.Set;

@Data
public class ShopNewComerComponentsListParam {
    private int limit;
    private Set<Integer> skuIds;
}
