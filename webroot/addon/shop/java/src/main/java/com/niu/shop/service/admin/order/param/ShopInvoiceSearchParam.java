package com.niu.shop.service.admin.order.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 发票参数
 */
@Data
public class ShopInvoiceSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer headerType;

    private String headerName;

    private String isInvoice;

    private String[] createTime;

    private String[] invoiceTime;



}
