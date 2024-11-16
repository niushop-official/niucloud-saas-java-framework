package com.niu.shop.service.api.order.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderNumVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long waitPay;

    private Long waitShipping;

    private Long waitTake;

    private Long evaluate;

    private Long refund;
}
