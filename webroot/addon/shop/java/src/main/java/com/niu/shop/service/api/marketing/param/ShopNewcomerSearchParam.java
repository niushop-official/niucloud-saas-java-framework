package com.niu.shop.service.api.marketing.param;

import lombok.Data;

@Data
public class ShopNewcomerSearchParam {
    private String keyword;
    private String goodsCategory;
    private Integer brandId;
    private String startPrice;
    private String endPrice;
    private String order;
    private String sort;
}
