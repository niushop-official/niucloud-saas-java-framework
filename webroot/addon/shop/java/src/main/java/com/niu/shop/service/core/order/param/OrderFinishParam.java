package com.niu.shop.service.core.order.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderFinishParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer orderId;

    private String mainType;

    private Integer mainId;
}
