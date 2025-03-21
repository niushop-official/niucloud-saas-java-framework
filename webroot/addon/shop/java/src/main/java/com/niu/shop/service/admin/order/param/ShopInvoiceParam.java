package com.niu.shop.service.admin.order.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

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
