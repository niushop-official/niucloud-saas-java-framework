package com.niu.shop.service.api.order.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 发票参数
 */
@Data
public class ShopInvoiceEditParam implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
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
