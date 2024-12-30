package com.niu.recharge.service.admin.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeParam {
    private Integer rechargeId;
    private Integer siteId;
    private String rechargeName;
    private BigDecimal faceValue;
    private BigDecimal buyPrice;
    private Integer point;
    private Integer growth;
    private String couponId;
    private Integer saleNum;
    private Integer sort;
    private Integer status;
    private String createTime;
    private String updateTime;
    private Object giftJson;
}
