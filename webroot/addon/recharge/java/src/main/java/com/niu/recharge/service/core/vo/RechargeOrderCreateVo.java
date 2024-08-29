package com.niu.recharge.service.core.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RechargeOrderCreateVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tradeType;

    private Integer tradeId;

}
