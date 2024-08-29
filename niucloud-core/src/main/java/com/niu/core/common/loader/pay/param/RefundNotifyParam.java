package com.niu.core.common.loader.pay.param;

import com.niu.core.enums.pay.OnliepayStatusEnum;
import com.niu.core.enums.pay.RefundTransferStatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 支付记录参数
*/
@Data
public class RefundNotifyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String refundNo;

    private String outTradeNo;

    private RefundTransferStatusEnum refundStatus;

    private String type;
}
