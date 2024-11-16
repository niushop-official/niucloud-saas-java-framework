package com.niu.shop.service.api.order.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeliveryPackageSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String mobile;
}
