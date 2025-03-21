package com.niu.shop.service.api.goods.refund.param;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Data
public class ApplyRefundParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "缺少参数order_id")
    private Integer orderId;

    @NotNull(message = "缺少参数order_goods_id")
    private Integer orderGoodsId;

    @NotNull(message = "缺少参数refund_type")
    private String refundType;

    @NotNull(message = "缺少参数apply_money")
    @Min(value = 0, message = "退款金额不能小于0")
    private BigDecimal applyMoney;

    private String reason = "";

    private String remark = "";

    private List<String> voucher = new LinkedList<>();
}
