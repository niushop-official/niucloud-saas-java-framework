package com.niu.shop.service.admin.refund.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单退款参数
 */
@Data
public class AuditRefundGoodsParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderRefundNo;

    @NotNull(message = "is_agree参数缺失")
    private Integer isAgree;

    private String shopReason = "";
}
