package com.niu.recharge.service.core.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RechargeOrderCreateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String memberMessage;

    private BigDecimal rechargeMoney;

    private Integer rechargeId;

    private Integer siteId;

    private Integer memberId;
}
