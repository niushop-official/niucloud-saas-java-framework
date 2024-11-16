package com.niu.shop.service.admin.stat.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderStatVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long waitPayOrder = 0L;

    private Long waitDeliveryOrder = 0L;

    private Long waitTakeOrder = 0L;

    private Long refundOrder = 0L;
}
