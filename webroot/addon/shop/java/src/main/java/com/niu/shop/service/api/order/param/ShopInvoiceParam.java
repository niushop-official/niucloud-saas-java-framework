package com.niu.shop.service.api.order.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 发票参数
*/
@Data
public class ShopInvoiceParam implements Serializable {

    private static final long serialVersionUID = 1L;


    @NotNull(message = "invoiceNumber参数缺失")
    private String invoiceNumber;

    @NotNull(message = "invoiceVoucher参数缺失")
    private String invoiceVoucher;

    private String remark;


}
