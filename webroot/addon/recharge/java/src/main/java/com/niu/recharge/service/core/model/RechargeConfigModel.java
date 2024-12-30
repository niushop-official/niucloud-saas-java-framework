package com.niu.recharge.service.core.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeConfigModel {
    private Integer isUse;
    private String rechargeExplain;
    private BigDecimal minPrice;
    private Integer closeLength;
}
