package com.niu.shop.service.core.order.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单配置参数
 */
@Data
public class OrderConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单关闭配置
     */
    private Boolean isClose = false; // 是否关闭
    private Integer closeLength = 60; // 关闭时长

    /**
     * 订单评价配置
     */
    private Integer isEvaluate = 0; // 是否开启订单评价
    private Integer evaluateIsToExamine = 0; // 完成订单是否需要审核
    private Integer evaluateIsShow = 0; // 是否显示评价

    /**
     * 订单完成配置
     */
    private Boolean isFinish = false; // 是否自动完成
    private Integer finishLength = 7; // 完成时长

    /**
     * 订单发票配置
     */
    private String isInvoice = "2"; // 是否需要发票
    private String[] invoiceType = {}; // 发票类型
    private String[] invoiceContent = {}; // 发票内容

    /**
     * 订单退款配置
     */
    private Boolean noAllowRefund = false; // 退款允许
    private Integer refundLength = 30; // 退款时长


}
