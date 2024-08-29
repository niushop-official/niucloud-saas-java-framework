package com.niu.core.common.loader.pay.param;

import com.niu.core.entity.pay.Pay;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付记录参数
*/
@Data
public class PayParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String channel;

    private String tradeType;

    private Integer tradeId;

    private String type;

    private String openid;

    private String voucher;

    private String refundUrl;

    private String quitUrl;

    private String notifyUrl;

    private Integer buyerId;

    private Pay pay;
}
