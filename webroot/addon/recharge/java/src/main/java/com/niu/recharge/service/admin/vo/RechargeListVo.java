package com.niu.recharge.service.admin.vo;

import cn.hutool.json.JSONObject;
import com.niu.recharge.service.core.vo.RechargeGiftContentVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RechargeListVo {
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
    private List<RechargeGiftContentVo> giftContent;
}
