package com.niu.core.enums.pay;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {
    WAITPAY("待支付", 0, 0),
    WAITDELIVERY("待发货", 1, 1),
    WAIT_TAKE("待收货", 2, 1),
    FINISH("已完成", 3, 0),
    CLOSE("已关闭", -1, 0)
    ;
    private final String name; //状态名称
    private final Integer status; //状态码
    private final Integer IsRefund; //是否退款
}
