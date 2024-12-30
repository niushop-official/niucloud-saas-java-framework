package com.niu.shop.service.core.marketing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ShopManjianRule {

    @JsonProperty("limit")
    private String limit;
    @JsonProperty("is_discount")
    private Boolean isDiscount;
    @JsonProperty("is_free_shipping")
    private Boolean isFreeShipping;
    @JsonProperty("is_give_point")
    private Boolean isGivePoint;
    @JsonProperty("is_give_coupon")
    private Boolean isGiveCoupon;
    @JsonProperty("is_give_goods")
    private Boolean isGiveGoods;
    @JsonProperty("is_give_balance")
    private Boolean isGiveBalance;
    @JsonProperty("discount_type")
    private Integer discountType;
    @JsonProperty("discount_money")
    private String discountMoney;
    @JsonProperty("point")
    private String point;
    @JsonProperty("balance")
    private String balance;
    @JsonProperty("goods_ids")
    private List<Integer> goodsIds;
    @JsonProperty("goods")
    private List<GoodsDTO> goods;
    @JsonProperty("couponIds")
    private List<Integer> couponIds;
    @JsonProperty("coupon")
    private List<ShopManjianCoupon> coupon;

    @NoArgsConstructor
    @Data
    public static class GoodsDTO {
        @JsonProperty("goods_cover_thumb_small")
        private String goodsCoverThumbSmall;
        @JsonProperty("site_id")
        private Integer siteId;
        @JsonProperty("goods_id")
        private Integer goodsId;
        @JsonProperty("goods_cover")
        private String goodsCover;
        @JsonProperty("goods_name")
        private String goodsName;
        @JsonProperty("sku_id")
        private Integer skuId;
        @JsonProperty("sku_name")
        private String skuName;
        @JsonProperty("sku_image")
        private String skuImage;
        @JsonProperty("stock")
        private Integer stock;
        @JsonProperty("price")
        private BigDecimal price;
        @JsonProperty("goodsSku")
        private GoodsSkuDTO goodsSku;
        @JsonProperty("num")
        private Integer num;

        @NoArgsConstructor
        @Data
        public static class GoodsSkuDTO {
            @JsonProperty("sku_id")
            private Integer skuId;
            @JsonProperty("site_id")
            private Integer siteId;
            @JsonProperty("sku_name")
            private String skuName;
            @JsonProperty("sku_image")
            private String skuImage;
            @JsonProperty("sku_no")
            private String skuNo;
            @JsonProperty("goods_id")
            private Integer goodsId;
            @JsonProperty("sku_spec_format")
            private String skuSpecFormat;
            @JsonProperty("price")
            private BigDecimal price;
            @JsonProperty("market_price")
            private BigDecimal marketPrice;
            @JsonProperty("sale_price")
            private BigDecimal salePrice;
            @JsonProperty("cost_price")
            private BigDecimal costPrice;
            @JsonProperty("stock")
            private Integer stock;
            @JsonProperty("weight")
            private String weight;
            @JsonProperty("volume")
            private String volume;
            @JsonProperty("sale_num")
            private Integer saleNum;
            @JsonProperty("is_default")
            private Integer isDefault;
            @JsonProperty("member_price")
            private Object memberPrice;
        }
    }

}
