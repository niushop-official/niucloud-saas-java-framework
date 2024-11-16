package com.niu.shop.service.core.refund.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class RefundFinishParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderRefundNo;
}
