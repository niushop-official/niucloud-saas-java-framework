package com.niu.shop.service.admin.goods.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopManjianRuleContentVo {
    private String conditionType;
    private Long createTime;
    private Long endTime;
    private String goodsIds;
    private String goodsType;
    private String joinMemberType;
    private String labelIds;
    private String levelIds;
    private Integer manjianId;
    private String manjianName;
    private String remark;
    private String ruleJson;
    private String ruleType;
    private Integer siteId;
    private Long startTime;
    private Integer status;
    private BigDecimal totalBalance;
    private Integer totalCouponNum;
    private Integer totalGoodsNum;
    private Integer totalMemberNum;
    private BigDecimal totalOrderMoney;
    private Integer totalOrderNum;
    private Integer totalPoint;
    private boolean isJoin;
}
