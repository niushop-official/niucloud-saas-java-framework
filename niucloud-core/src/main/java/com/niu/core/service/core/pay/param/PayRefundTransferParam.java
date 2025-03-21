package com.niu.core.service.core.pay.param;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 支付记录参数
*/
@Data
public class PayRefundTransferParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    @NotNull(message = "refundNo参数缺失")
    @NotBlank(message = "退款单号不能为空")
    private String refundNo;

    @NotNull(message = "refund_type参数缺失")
    @NotBlank(message = "退款方式不能为空")
    private String refundType = "back";

    @NotNull(message = "voucher参数缺失")
    private String voucher;

}
