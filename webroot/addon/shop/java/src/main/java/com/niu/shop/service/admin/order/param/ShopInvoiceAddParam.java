package com.niu.shop.service.admin.order.param;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 发票实体
 */
@Data
public class ShopInvoiceAddParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;
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
    private String payVoucher;
    private BigDecimal money; // 开票金额
}