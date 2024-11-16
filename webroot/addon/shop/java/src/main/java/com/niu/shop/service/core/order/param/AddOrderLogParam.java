package com.niu.shop.service.core.order.param;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
public class AddOrderLogParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer orderId; // 订单id
    private String mainType; //
    private Integer mainId; // 操作人id
    private Integer status; // 订单状态
    private String type; //
    private String content; // 日志内容

}
