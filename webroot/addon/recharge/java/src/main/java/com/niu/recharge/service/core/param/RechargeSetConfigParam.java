package com.niu.recharge.service.core.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeSetConfigParam {
    private Integer isUse;
    private String rechargeExplain;
    private BigDecimal minPrice;
    private Integer closeLength;
}
