package com.niu.shop.service.api.goods.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.niu.shop.entity.goods.ShopGoodsSku;
import lombok.Data;

@Data
public class ShopGoodsRankGoodsListInfo {
    private Integer siteId;
    private Integer goodsId;
    private String goodsName;
    private String goodsCover;
    private String unit;
    private Integer saleNum;
    private String memberDiscount;
    private Integer statSaleNum;
    private Integer statCollectNum;
    private Integer statEvaluateNum;
    private Integer statAccessNum;
    private String createTime;
    private Integer goodsSort;
    private Integer sort;
    private String goodsCoverThumbMid;
    @JsonProperty("goodsSku")
    private ShopGoodsSku goodsSku;
    private Integer rankNum;
}