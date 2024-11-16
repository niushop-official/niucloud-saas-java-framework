package com.niu.shop.service.api.marketing.param;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ShopPointCalculateSearchParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderKey = "";
    private Delivery delivery = new Delivery();
    private List<SkuItem> skuData;
    private List<Integer> cartIds;
    private Discount discount;
    private JSONObject extendData;
    private Integer siteId;
    private Integer memberId;
    private String orderFrom;

    @Data
    public static class TakeAddress {
        private String lat;
        private String lng;
    }

    @Data
    public static class SkuItem {
        private Integer skuId;
        private Integer num;
    }

    @Data
    public static class Delivery {
        private String deliveryType = "";
        private Integer takeAddressId = 0;
        private Integer takeStoreId = 0;
        private TakeAddress takeAddress;
    }

    @Data
    public static class Discount {
        private Integer couponId;
    }
}
