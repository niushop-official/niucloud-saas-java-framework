package com.niu.shop.service.core.order.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class InvoiceCreateParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer headerType; // 抬头类型
    private String headerName; // 名称（发票抬头）
    private Integer type; // 发票类型
    private String name; // 发票内容
    private String taxNumber; // 公司税号
    private String mobile; // 开票人手机号
    private String email; // 开票人邮箱
    private String telephone; // 注册电话
    private String address; // 注册地址
    private String bankName; // 开户银行
    private String bankCardNumber; // 银行账号
}
