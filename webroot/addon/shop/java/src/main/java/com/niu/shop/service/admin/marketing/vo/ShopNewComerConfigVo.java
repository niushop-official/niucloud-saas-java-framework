package com.niu.shop.service.admin.marketing.vo;

import lombok.Data;

import java.util.List;

@Data
public class ShopNewComerConfigVo {
    private String activeStatus;
    private String activeDesc;
    private String goodsData;
    private String validityType;
    private Integer validityDay;
    private String validityTime;
    private String participationWay;
    private String appointTime;
    private Integer limitNum;
    private List<BannerList> bannerList;

    @Data
    public static class BannerList {
        private String imageUrl;
        private ToLink toLink;

        @Data
        public static class ToLink {
            private String name;
        }
    }
}
