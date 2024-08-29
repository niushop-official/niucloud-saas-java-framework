package com.niu.core.common.loader.pay.param;

import com.niu.core.enums.pay.RefundTransferStatusEnum;
import com.niu.core.enums.pay.TransferStatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 支付记录参数
*/
@Data
public class TransferNotifyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String transferNo;

    private TransferStatusEnum transferStatus;

    private String failReason;
}
