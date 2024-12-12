package com.niu.shop.service.api.marketing.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopNewcomerInfoVo {
    private BigDecimal newcomerPrice = BigDecimal.ZERO;
    private String newcomerDesc;
    private int limitNum;
}
