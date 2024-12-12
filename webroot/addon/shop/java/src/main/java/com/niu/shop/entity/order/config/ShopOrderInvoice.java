package com.niu.shop.entity.order.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单发票设置
 */
@Data
public class ShopOrderInvoice implements Serializable {

    private static final long serialVersionUID = 1L;
    private String isInvoice = "2"; // 是否需要发票
    private String[] invoiceType = new String[]{}; // 发票类型
    private String[] invoiceContent = new String[]{}; // 发票内容


}