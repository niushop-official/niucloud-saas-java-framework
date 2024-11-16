package com.niu.shop.service.core.order.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderPayParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer tradeId;

    private String outTradeNo = "";

    private String mainType;

    private Integer mainId;
}
