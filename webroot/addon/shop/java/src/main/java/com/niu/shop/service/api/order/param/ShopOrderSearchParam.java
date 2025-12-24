package com.niu.shop.service.api.order.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单参数
 */
@Data
public class ShopOrderSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;

    private String status;

    private String activityType;

    private String body;
}
