package com.niu.core.service.admin.pay.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.*;

/**
 * 支付记录参数
*/
@Data
public class PayParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "mainId参数缺失")
    private Integer mainId;

    @NotNull(message = "outTradeNo参数缺失")
    private String outTradeNo;

    @NotNull(message = "tradeType参数缺失")
    private String tradeType;

    @NotNull(message = "tradeId参数缺失")
    private Integer tradeId;

    @NotNull(message = "tradeNo参数缺失")
    private String tradeNo;

    @NotNull(message = "body参数缺失")
    private String body;

    @NotNull(message = "money参数缺失")
    private BigDecimal money;

    @NotNull(message = "voucher参数缺失")
    private String voucher;

    @NotNull(message = "status参数缺失")
    private Integer status;

    @NotNull(message = "json参数缺失")
    private String json;

    @NotNull(message = "payTime参数缺失")
    private Long payTime;

    @NotNull(message = "cancelTime参数缺失")
    private Long cancelTime;

    @NotNull(message = "type参数缺失")
    private String type;

    @NotNull(message = "mchId参数缺失")
    private String mchId;

    @NotNull(message = "mainType参数缺失")
    private String mainType;

    @NotNull(message = "channel参数缺失")
    private String channel;

    @NotNull(message = "failReason参数缺失")
    private String failReason;

}
