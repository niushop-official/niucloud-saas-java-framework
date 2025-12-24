package com.niu.shop.service.admin.order.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 发票参数
*/
@Data
public class ShopInvoiceAuditParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Integer> ids;


}
