package com.niu.shop.service.core.third_addon.order.vo;

import lombok.Data;

@Data
public class CoreOrderCreateResultVo {
    private Integer orderId;
    private Integer relateOrderId;
    private Integer relateOrderItemId;
    private Boolean isSuccess;
    private String reason;
}
