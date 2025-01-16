package com.niu.shop.service.api.marketing.param;

import lombok.Data;

import java.util.Set;

@Data
public class ShopNewComerComponentsListParam {
    private Integer limit;
    private Set<Integer> skuIds;
}
