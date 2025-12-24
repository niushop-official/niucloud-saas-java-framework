package com.niu.shop.service.core.marketing.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDetail {

    @JsonProperty("goods_id")
    private Integer goodsId;
    @JsonProperty("sku_id")
    private Integer skuId;
    @JsonProperty("point")
    private Integer point;
    @JsonProperty("price")
    private BigDecimal price;
    @JsonProperty("limit_num")
    private Integer limitNum;
    @JsonProperty("stock")
    private Integer stock;
    @JsonProperty("is_enabled")
    private Integer isEnabled;
}