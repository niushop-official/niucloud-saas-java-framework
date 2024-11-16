package com.niu.shop.service.core.delivery.param;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class LatlngParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal Lat;

    private BigDecimal Lng;
}
