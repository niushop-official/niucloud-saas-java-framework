package com.niu.shop.service.core.marketing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ShopNewcomerGoodsData {

    @JsonProperty("goods_id")
    private Integer goodsId;
    @JsonProperty("sku_id")
    private Integer skuId;
    @JsonProperty("price")
    private String price;
    @JsonProperty("newcomer_price")
    private String newcomerPrice;
}
