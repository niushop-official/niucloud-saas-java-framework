package com.niu.shop.entity.order.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单退款配置
 */
@Data
public class ShopOrderRefund implements Serializable {

    private static final long serialVersionUID = 1L;
    private Boolean noAllowRefund; // 退款允许
    private Integer refundLength; // 退款时长

}