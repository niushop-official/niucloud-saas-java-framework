package com.niu.recharge.service.admin.vo;

import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Data
public class RechargeInitVo {
    private Integer rechargeId;
    private Integer siteId;
    private String rechargeName;
    private BigDecimal faceValue;
    private BigDecimal buyPrice;
    private Integer point;
    private Integer growth;
    private Integer saleNum;
    private Integer sort;
    private Integer status;
    private String createTime;
    private JSONObject giftJson;
    private String statusName;
}
