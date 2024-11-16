package com.niu.shop.entity.order.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单评价设置
 */
@Data
public class ShopOrderEvaluate implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer isEvaluate; // 是否开启订单评价
    private Integer evaluateIsToExamine; // 完成订单是否需要审核
    private Integer evaluateIsShow; // 是否显示评价


}