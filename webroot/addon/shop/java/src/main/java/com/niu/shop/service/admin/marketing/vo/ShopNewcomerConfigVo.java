package com.niu.shop.service.admin.marketing.vo;

import cn.hutool.json.JSON;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.niu.shop.service.core.marketing.model.ShopNewcomerBannerList;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ShopNewcomerConfigVo {
    private Integer activeId;
    private String activeStatus;
    private String activeDesc;
    private String validityType;
    private Integer validityDay;
    private String validityTime;
    private String participationWay;
    private String appointTime;
    private Integer limitNum;
    private List<ShopNewcomerBannerList> bannerList;
    private List<ActiveGoods> activeGoods;

    @Data
    public static class ActiveGoods {
        @JsonProperty("goods_id")
        private Integer goodsId;
        @JsonProperty("goods_name")
        private String goodsName;
        @JsonProperty("goods_type")
        private String goodsType;
        @JsonProperty("goods_type_name")
        private String goodsTypeName;
        @JsonProperty("sku_name")
        private String skuName;
        @JsonProperty("sku_id")
        private Integer skuId;
        @JsonProperty("sku_image")
        private String skuImage;
        private BigDecimal price;
        private Integer stock;
        @JsonProperty("active_goods_value")
        private JSON activeGoodsValue;
        private Goods goods;
        @JsonProperty("goodsSkuOne")
        private GoodsSkuOne goodsSkuOne;
    }

    @NoArgsConstructor
    @Data
    public static class Goods {

        private Integer siteId;
        @JsonProperty("goods_type_name")
        private String goodsTypeName;
        @JsonProperty("goods_id")
        private Integer goodsId;
        @JsonProperty("goods_name")
        private String goodsName;
        @JsonProperty("goods_type")
        private String goodsType;

        private String goodsCover;
    }

    @NoArgsConstructor
    @Data
    public static class GoodsSkuOne {

        @JsonProperty("sku_id")
        private Integer skuId;
        @JsonProperty("sku_name")
        private String skuName;
        @JsonProperty("sku_image")
        private String skuImage;
        @JsonProperty("goods_id")
        private Integer goodsId;
        @JsonProperty("price")
        private BigDecimal price;
        @JsonProperty("stock")
        private Integer stock;
    }
}
