package com.niu.shop.service.core.refund.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单主动退款参数
 */
@Data
public class ActiveRefundParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "order_goods_ids参数缺失")
    private Integer[] orderGoodsIds;

    private BigDecimal refundMoney;

    private String shopActiveRefundRemark = "";

    private String mainType;

    private Integer mainId;

    private Integer siteId;
}
