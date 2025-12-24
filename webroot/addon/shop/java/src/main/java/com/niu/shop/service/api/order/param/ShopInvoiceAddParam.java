package com.niu.shop.service.api.order.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 发票实体
 */
@Data
public class ShopInvoiceAddParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> tradeIds;
    private Integer headerType;
    private String headerName;
    private Integer type;
    private String name;
    private String taxNumber;
    private String mobile;
    private String email;
    private String telephone;
    private String address;
    private String bankName;
    private String bankCardNumber;
    private BigDecimal money;
    private String payVoucher;
}