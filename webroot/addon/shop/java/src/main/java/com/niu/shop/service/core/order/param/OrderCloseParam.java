package com.niu.shop.service.core.order.param;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class OrderCloseParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer orderId;

    private Integer siteId;

    private String closeType;

    private String closeRemark = "";

    private String mainType;

    private Integer mainId;
}
