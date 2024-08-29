package com.niu.core.service.core.pay.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付记录参数
 */
@Data
public class PayRefundSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String refundNo;

    private String outTradeNo;

    private String type;

    private String channel;

    private BigDecimal money;

    private String reason;

    private String status;

    private Integer refundTime;

    private Integer closeTime;

    private String failReason;

    private String voucher;

    private String tradeType;

    private String tradeId;

    private String refundType;

    private String mainType;

    private Integer mainId;

    private String payRefundNo;

}
