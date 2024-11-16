package com.niu.shop.service.api.marketing.vo;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ShopPointExchangeInfoVo {

    @JsonProperty("sku_image_thumb_small")
    private String skuImageThumbSmall;
    @JsonProperty("sku_image_thumb_mid")
    private String skuImageThumbMid;
    @JsonProperty("sku_image_thumb_big")
    private String skuImageThumbBig;
    @JsonProperty("sku_id")
    private Integer skuId;
    @JsonProperty("sku_name")
    private String skuName;
    @JsonProperty("sku_image")
    private String skuImage;
    @JsonProperty("sku_no")
    private String skuNo;
    @JsonProperty("goods_id")
    private Integer goodsId;
    @JsonProperty("site_id")
    private Integer siteId;
    @JsonProperty("sku_spec_format")
    private String skuSpecFormat;
    @JsonProperty("price")
    private String price;
    @JsonProperty("market_price")
    private String marketPrice;
    @JsonProperty("sale_price")
    private String salePrice;
    @JsonProperty("stock")
    private String stock;
    @JsonProperty("weight")
    private String weight;
    @JsonProperty("volume")
    private String volume;
    @JsonProperty("sale_num")
    private Integer saleNum;
    @JsonProperty("is_default")
    private Integer isDefault;
    @JsonProperty("member_price")
    private String memberPrice;
    @JsonProperty("goods")
    private GoodsDTO goods;
    @JsonProperty("skuList")
    private List<SkuListDTO> skuList;
    @JsonProperty("goodsSpec")
    private List<?> goodsSpec;
    @JsonProperty("type")
    private String type;
    @JsonProperty("type_name")
    private String typeName;
    @JsonProperty("service")
    private List<ServiceDTO> service;
    @JsonProperty("label_info")
    private List<LabelInfoDTO> labelInfo;
    @JsonProperty("evaluate_is_show")
    private Integer evaluateIsShow;
    @JsonProperty("point")
    private String point;
    @JsonProperty("exchange_id")
    private Integer exchangeId;

    @NoArgsConstructor
    @Data
    public static class GoodsDTO {
        @JsonProperty("goods_type_name")
        private String goodsTypeName;
        @JsonProperty("goods_cover_thumb_small")
        private String goodsCoverThumbSmall;
        @JsonProperty("goods_cover_thumb_mid")
        private String goodsCoverThumbMid;
        @JsonProperty("goods_cover_thumb_big")
        private String goodsCoverThumbBig;
        @JsonProperty("delivery_type_list")
        private DeliveryTypeListDTO deliveryTypeList;
        @JsonProperty("goods_image_thumb_small")
        private List<String> goodsImageThumbSmall;
        @JsonProperty("goods_image_thumb_mid")
        private List<String> goodsImageThumbMid;
        @JsonProperty("goods_image_thumb_big")
        private List<String> goodsImageThumbBig;
        @JsonProperty("goods_brand")
        private GoodsBrandDTO goodsBrand;
        @JsonProperty("goods_id")
        private Integer goodsId;
        @JsonProperty("is_discount")
        private Integer isDiscount;
        @JsonProperty("site_id")
        private Integer siteId;
        @JsonProperty("goods_name")
        private String goodsName;
        @JsonProperty("goods_type")
        private String goodsType;
        @JsonProperty("sub_title")
        private String subTitle;
        @JsonProperty("goods_cover")
        private String goodsCover;
        @JsonProperty("goods_image")
        private String goodsImage;
        @JsonProperty("goods_desc")
        private String goodsDesc;
        @JsonProperty("brand_id")
        private Integer brandId;
        @JsonProperty("label_ids")
        private List<String> labelIds;
        @JsonProperty("service_ids")
        private List<String> serviceIds;
        @JsonProperty("unit")
        private String unit;
        @JsonProperty("stock")
        private Integer stock;
        @JsonProperty("sale_num")
        private Integer saleNum;
        @JsonProperty("status")
        private Integer status;
        @JsonProperty("is_free_shipping")
        private Integer isFreeShipping;
        @JsonProperty("fee_type")
        private String feeType;
        @JsonProperty("delivery_type")
        private List<String> deliveryType;
        @JsonProperty("delivery_money")
        private String deliveryMoney;
        @JsonProperty("delivery_template_id")
        private Integer deliveryTemplateId;
        @JsonProperty("goods_category")
        private List<Integer> goodsCategory;
        @JsonProperty("attr_id")
        private Integer attrId;
        @JsonProperty("attr_format")
        private String attrFormat;
        @JsonProperty("member_discount")
        private String memberDiscount;
        @JsonProperty("goods_video")
        private String goodsVideo;
        @JsonProperty("poster_id")
        private Integer posterId;
        @JsonProperty("virtual_receive_type")
        private String virtualReceiveType;
        @JsonProperty("is_collect")
        private Integer isCollect;

        @NoArgsConstructor
        @Data
        public static class DeliveryTypeListDTO {
            @JsonProperty("express")
            private ExpressDTO express;
            @JsonProperty("local_delivery")
            private LocalDeliveryDTO localDelivery;
            @JsonProperty("store")
            private StoreDTO store;

            @NoArgsConstructor
            @Data
            public static class ExpressDTO {
                @JsonProperty("name")
                private String name;
                @JsonProperty("status")
                private String status;
                @JsonProperty("key")
                private String key;
                @JsonProperty("desc")
                private String desc;
            }

            @NoArgsConstructor
            @Data
            public static class LocalDeliveryDTO {
                @JsonProperty("name")
                private String name;
                @JsonProperty("status")
                private String status;
                @JsonProperty("key")
                private String key;
                @JsonProperty("desc")
                private String desc;
            }

            @NoArgsConstructor
            @Data
            public static class StoreDTO {
                @JsonProperty("name")
                private String name;
                @JsonProperty("status")
                private String status;
                @JsonProperty("key")
                private String key;
                @JsonProperty("desc")
                private String desc;
            }
        }

        @NoArgsConstructor
        @Data
        public static class GoodsBrandDTO {
            @JsonProperty("brand_id")
            private Integer brandId;
            @JsonProperty("brand_name")
            private String brandName;
            @JsonProperty("logo")
            private String logo;
        }
    }

    @NoArgsConstructor
    @Data
    public static class SkuListDTO {
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
        private String price;
        @JsonProperty("market_price")
        private String marketPrice;
        @JsonProperty("sale_price")
        private String salePrice;
        @JsonProperty("stock")
        private String stock;
        @JsonProperty("weight")
        private String weight;
        @JsonProperty("volume")
        private String volume;
        @JsonProperty("is_default")
        private Integer isDefault;
        @JsonProperty("member_price")
        private String memberPrice;
        @JsonProperty("limit_num")
        private String limitNum;
        @JsonProperty("point")
        private String point;
    }

    @NoArgsConstructor
    @Data
    public static class ServiceDTO {
        @JsonProperty("service_id")
        private Integer serviceId;
        @JsonProperty("service_name")
        private String serviceName;
        @JsonProperty("image")
        private String image;
        @JsonProperty("desc")
        private String desc;
    }

    @NoArgsConstructor
    @Data
    public static class LabelInfoDTO {
        @JsonProperty("label_id")
        private Integer labelId;
        @JsonProperty("label_name")
        private String labelName;
        @JsonProperty("memo")
        private String memo;
        @JsonProperty("style_type")
        private String styleType;
        @JsonProperty("color_json")
        private ColorJsonDTO colorJson;
        @JsonProperty("icon")
        private String icon;

        @NoArgsConstructor
        @Data
        public static class ColorJsonDTO {
            @JsonProperty("text_color")
            private String textColor;
            @JsonProperty("bg_color")
            private String bgColor;
            @JsonProperty("border_color")
            private String borderColor;
        }
    }
}
