package com.niu.shop.service.api.goods.refund.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单退款参数
 */
@Data
public class RefundSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;

}
