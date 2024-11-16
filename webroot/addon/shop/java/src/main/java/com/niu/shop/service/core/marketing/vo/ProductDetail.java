package com.niu.shop.service.core.marketing.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductDetail {

    @JsonProperty("goods_id")
    private Integer goodsId;
    @JsonProperty("sku_id")
    private Integer skuId;
    @JsonProperty("point")
    private String point;
    @JsonProperty("price")
    private String price;
    @JsonProperty("limit_num")
    private String limitNum;
    @JsonProperty("stock")
    private String stock;
    @JsonProperty("is_enabled")
    private Integer isEnabled;
}