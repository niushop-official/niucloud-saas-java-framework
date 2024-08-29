package com.niu.core.common.loader.pay.param;

import com.niu.core.entity.pay.Pay;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付记录参数
*/
@Data
public class RefundParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String outTradeNo;

    private BigDecimal money;

    private BigDecimal total;

    private String refundNo;

    private String voucher;

    private String notifyUrl;

    private Integer memberId;
}
