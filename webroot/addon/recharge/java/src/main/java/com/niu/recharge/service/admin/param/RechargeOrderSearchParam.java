package com.niu.recharge.service.admin.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class RechargeOrderSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;

    private Integer orderStatus;

    private String orderFrom;

    private String[] createTime;

    private String[] payTime;

    private float startMoney;

    private float endMoney;

    private Integer memberId = 0;
}
