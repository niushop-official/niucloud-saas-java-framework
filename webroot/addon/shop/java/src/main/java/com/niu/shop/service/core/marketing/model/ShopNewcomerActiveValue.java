package com.niu.shop.service.core.marketing.model;

import lombok.Data;

import java.util.List;

@Data
public class ShopNewcomerActiveValue {
    private String validityType;
    private Integer validityDay;
    private long validityTime;
    private String participationWay;
    private long appointTime;
    private Integer limitNum;
    private List<ShopNewcomerBannerList> bannerList;
}
