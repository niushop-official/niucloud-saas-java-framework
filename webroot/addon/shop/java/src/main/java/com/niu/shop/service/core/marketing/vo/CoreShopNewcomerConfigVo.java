package com.niu.shop.service.core.marketing.vo;

import com.niu.shop.service.core.marketing.model.ShopNewcomerBannerList;
import lombok.Data;

import java.util.List;

@Data
public class CoreShopNewcomerConfigVo {
    private Integer activeId;
    private String activeStatus;
    private String validityType;
    private Integer validityDay;
    private String validityTime;
    private String activeDesc;
    private String participationWay;
    private String appointTime;
    private Integer limitNum;
    private Integer siteId;
    private List<ShopNewcomerBannerList> bannerList;
}
