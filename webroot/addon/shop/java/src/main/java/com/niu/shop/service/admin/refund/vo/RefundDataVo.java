package com.niu.shop.service.admin.refund.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RefundDataVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal refundMoney;

    private Integer isRefundDelivery;

    private BigDecimal refundOrderGoodsMoney;

    private BigDecimal refundDeliveryMoney;
}
