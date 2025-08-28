package com.niu.shop.service.admin.delivery.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class TradeTimeItemParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer startTime;

    private Integer endTime;

}
