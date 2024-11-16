package com.niu.shop.service.core.shop.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class StatVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderNum = 0; // 订单总数

    private BigDecimal saleMoney = new BigDecimal(0); // 销售总额

    private BigDecimal refundMoney = new BigDecimal(0); // 退款总额

    private Integer accessSum = 0; // 访问数
}
