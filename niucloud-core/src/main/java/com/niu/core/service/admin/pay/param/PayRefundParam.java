package com.niu.core.service.admin.pay.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.*;

/**
 * 支付记录参数
*/
@Data
public class PayRefundParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "refundNo参数缺失")
    private String refundNo;

    @NotNull(message = "outTradeNo参数缺失")
    private String outTradeNo;

    @NotNull(message = "type参数缺失")
    private String type;

    @NotNull(message = "channel参数缺失")
    private String channel;

    @NotNull(message = "money参数缺失")
    private BigDecimal money;

    @NotNull(message = "reason参数缺失")
    private String reason;

    @NotNull(message = "status参数缺失")
    private String status;

    @NotNull(message = "refundTime参数缺失")
    private Long refundTime;

    @NotNull(message = "closeTime参数缺失")
    private Long closeTime;

    @NotNull(message = "failReason参数缺失")
    private String failReason;

    @NotNull(message = "voucher参数缺失")
    private String voucher;

    @NotNull(message = "tradeType参数缺失")
    private String tradeType;

    @NotNull(message = "tradeId参数缺失")
    private String tradeId;

    @NotNull(message = "refundType参数缺失")
    private String refundType;

    @NotNull(message = "mainType参数缺失")
    private String mainType;

    @NotNull(message = "mainId参数缺失")
    private Integer mainId;

    @NotNull(message = "payRefundNo参数缺失")
    private String payRefundNo;

}
