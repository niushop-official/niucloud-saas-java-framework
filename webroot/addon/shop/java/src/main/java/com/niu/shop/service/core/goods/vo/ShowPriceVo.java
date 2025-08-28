package com.niu.shop.service.core.goods.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ShowPriceVo {

    private BigDecimal showPrice;

    private String showType = "";

    private Integer activeId;
}
