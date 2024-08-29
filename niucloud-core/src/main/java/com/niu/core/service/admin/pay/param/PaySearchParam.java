package com.niu.core.service.admin.pay.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付记录参数
 */
@Data
public class PaySearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer mainId;

    private String outTradeNo;

    private String tradeType;

    private Integer tradeId;

    private String tradeNo;

    private String body;

    private BigDecimal money;

    private String voucher;

    private Integer status;

    private String json;

    private Integer payTime;

    private Integer cancelTime;

    private String type;

    private String mchId;

    private String mainType;

    private String channel;

    private String failReason;

}
