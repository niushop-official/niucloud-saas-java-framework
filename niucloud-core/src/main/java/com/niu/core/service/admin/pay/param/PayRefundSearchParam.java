package com.niu.core.service.admin.pay.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付记录参数
 */
@Data
public class PayRefundSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String refundNo;

    private String status;

    private String[] createTime;

}
