package com.niu.shop.service.core.order.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderCouponListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer couponId; // 优惠券id
    private Integer memberId; // 会员id
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 领取时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long expireTime; // 过期时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long useTime; // 使用时间
    private String type; // 优惠券类型
    private Integer status; // 状态
    private String title; // 优惠券名称
    private BigDecimal price; // 面值
    private BigDecimal minConditionMoney; // 最低使用门槛
    private Boolean isNormal;
    private String error;
}
