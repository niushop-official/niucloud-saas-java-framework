package com.niu.shop.service.api.refund.param;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Data
public class EditRefundParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderRefundNo;

    @NotNull(message = "缺少参数refund_type")
    private String refundType;

    @NotNull(message = "缺少参数apply_money")
    @Min(value = 0, message = "退款金额不能小于0")
    private BigDecimal applyMoney;

    private String reason = "";

    private String remark = "";

    private List<String> voucher = new LinkedList<>();
}
