package com.niu.shop.service.core.order.param;

import cn.hutool.json.JSONObject;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class OrderCalculateParam implements Serializable {
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
    private List<ImpulseBuyGoods> impulseBuyGoods;
    private Integer isNeedRecalculate = 0;

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
    }

    @Data
    public static class Discount {
        private Integer couponId;
    }

    @Data
    public static class ImpulseBuyGoods {
        private Integer impulseBuyGoodsId;
        private Integer skuId;
        private Integer num;
    }
}
