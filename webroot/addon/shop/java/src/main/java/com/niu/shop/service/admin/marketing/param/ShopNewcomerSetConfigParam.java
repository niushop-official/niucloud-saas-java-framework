package com.niu.shop.service.admin.marketing.param;

import com.niu.shop.service.core.marketing.model.ShopNewcomerBannerList;
import lombok.Data;

import java.util.List;

@Data
public class ShopNewcomerSetConfigParam {
    private String activeStatus;
    private String activeDesc;
    private String goodsData;
    private String validityType;
    private Integer validityDay;
    private String validityTime;
    private String participationWay;
    private String appointTime;
    private Integer limitNum;
    private List<ShopNewcomerBannerList> bannerList;
}
