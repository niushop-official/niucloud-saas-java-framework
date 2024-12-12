package com.niu.shop.service.admin.marketing.param;

import lombok.Data;

import java.util.List;

@Data
public class ShopNewcomerSelectSkuParam {
    private String keyword;
    private String goodsCategory;
    private String goodsType;
    private List<Integer> goodsIds;
    private List<Integer> verifyGoodsIds;
    private List<Integer> verifySkuIds;
}
