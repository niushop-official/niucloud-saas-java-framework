package com.niu.shop.service.core.goods.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsAddStatParam {
    private Integer siteId;
    private Integer goodsId;
    private int saleNum;
    private int evaluateNum;
    private int cartNum;
    private int goodsVisitMemberCount;
    private int collectNum;
    private int refundNum;
    private int payNum;
    private int accessNum;
    private BigDecimal payMoney;
    private BigDecimal refundMoney;
}
