package com.niu.shop.service.api.goods.param;

import lombok.Data;

@Data
public class ShopGoodsRankGoodsPageParam {
    private Integer rankId;
    private Integer page;
    private Integer limit;
}
