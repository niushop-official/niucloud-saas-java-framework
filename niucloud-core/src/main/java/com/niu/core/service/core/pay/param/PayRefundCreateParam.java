package com.niu.core.service.core.pay.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付记录参数
*/
@Data
public class PayRefundCreateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "outTradeNo参数缺失")
    private String outTradeNo;

    @NotNull(message = "money参数缺失")
    private BigDecimal money;

    private String reason = "";

    private String tradeType = "";

    private String tradeId = "";

}
