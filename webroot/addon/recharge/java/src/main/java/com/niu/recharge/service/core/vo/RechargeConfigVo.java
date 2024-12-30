package com.niu.recharge.service.core.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeConfigVo {
    private Integer isUse;
    private String rechargeExplain;
    private BigDecimal minPrice;
    private Integer closeLength;
}
