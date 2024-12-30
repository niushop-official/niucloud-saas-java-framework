package com.niu.recharge.service.api.vo;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeListInfo {
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
    private JSONObject giftJson;
}
