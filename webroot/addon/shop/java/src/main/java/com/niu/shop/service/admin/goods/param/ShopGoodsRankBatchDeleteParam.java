package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import java.util.List;

@Data
public class ShopGoodsRankBatchDeleteParam {
    private List<Integer> rankId;
}
