package com.niu.core.common.loader.pay.param;

import com.niu.core.entity.pay.PayTransfer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付记录参数
*/
@Data
public class TransferParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private PayTransfer transfer;

    private String notifyUrl;
}
