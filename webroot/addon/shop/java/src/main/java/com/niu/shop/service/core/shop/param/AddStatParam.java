package com.niu.shop.service.core.shop.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 配送方式设置参数
*/
@Data
public class AddStatParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderNum; // 订单总数
    private BigDecimal saleMoney; // 销售总额
    private BigDecimal refundMoney; // 退款总额
    private Integer accessSum; // 访问数
}
