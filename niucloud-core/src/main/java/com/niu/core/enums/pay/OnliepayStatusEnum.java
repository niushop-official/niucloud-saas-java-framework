package com.niu.core.enums.pay;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付状态枚举
 */
@Getter
@AllArgsConstructor
public enum OnliepayStatusEnum {
    SUCCESS("支付成功", "SUCCESS"),
    REFUND("转入退款", "REFUND"),
    NOTPAY("未支付", "NOTPAY"),
    CLOSED("已关闭", "CLOSED"),
    REVOKED("已撤销", "REVOKED"),
    USERPAYING("用户支付中", "USERPAYING"),
    PAYERROR("支付失败", "PAYERROR"),
    ;

    private final String name;

    private final String status;
}
