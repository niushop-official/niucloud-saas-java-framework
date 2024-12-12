package com.niu.shop.service.admin.marketing.param;

import lombok.Data;

import java.util.List;

@Data
public class ShopNewcomerSelectPageParam {
    private String keyword;
    private String goodsCategory;
    private String goodsType;
    private String selectType;
    private List<Integer> goodsIds;
    private List<Integer> skuIds;
    private List<Integer> verifyGoodsIds;
    private List<Integer> verifySkuIds;
}
