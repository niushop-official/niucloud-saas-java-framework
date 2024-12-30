package com.niu.shop.service.core.marketing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ShopManjianCoupon {

    @JsonProperty("price")
    private String price;
    @JsonProperty("title")
    private String title;
    @JsonProperty("type_name")
    private String typeName;
    @JsonProperty("coupon_id")
    private Integer couponId;
    @JsonProperty("num")
    private Integer num;

    private String couponName;
}
