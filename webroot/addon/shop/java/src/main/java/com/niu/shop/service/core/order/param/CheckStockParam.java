package com.niu.shop.service.core.order.param;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckStockParam {

    private Integer goodsId;

    private Integer skuId;

    private Integer num;

    private String goodsName;
}
