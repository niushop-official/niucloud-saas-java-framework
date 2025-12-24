package com.niu.shop.service.core.goods.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsStatParam {
    private Long time;
    private Integer goodsId;
    private Integer siteId;
    private Integer saleNum;
}
