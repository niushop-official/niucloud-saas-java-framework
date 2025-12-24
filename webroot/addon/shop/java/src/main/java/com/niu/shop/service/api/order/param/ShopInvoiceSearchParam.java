package com.niu.shop.service.api.order.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 发票参数
 */
@Data
public class ShopInvoiceSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer status;
}
