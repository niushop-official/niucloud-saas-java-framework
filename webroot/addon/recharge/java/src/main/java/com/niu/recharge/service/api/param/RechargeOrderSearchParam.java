package com.niu.recharge.service.api.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class RechargeOrderSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderStatus;
}
