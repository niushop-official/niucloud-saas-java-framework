package com.niu.core.common.loader.pay.param;

import com.niu.core.entity.pay.Pay;
import com.niu.core.enums.pay.OnliepayStatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 支付记录参数
*/
@Data
public class PayNotifyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String type;

    private String tradeNo;

    private String outTradeNo;

    private String voucher;

    private String mchId;

    private OnliepayStatusEnum payStatus;
}
