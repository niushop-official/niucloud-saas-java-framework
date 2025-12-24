package com.niu.shop.service.api.goods.param;

import lombok.Data;

@Data
public class GoodsRankComponentParam {
    private Integer rankId;

    private Integer limit = 3;
}
