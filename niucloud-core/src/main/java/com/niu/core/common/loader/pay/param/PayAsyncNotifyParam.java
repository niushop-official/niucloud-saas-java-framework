package com.niu.core.common.loader.pay.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付记录参数
*/
@Data
public class PayAsyncNotifyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String type;

    private String channel;

    private String action;

}
