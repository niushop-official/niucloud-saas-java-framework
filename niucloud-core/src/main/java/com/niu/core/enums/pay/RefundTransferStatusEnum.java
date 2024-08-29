package com.niu.core.enums.pay;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 退款转账状态枚举
 */
@Getter
@AllArgsConstructor
public enum RefundTransferStatusEnum {
    SUCCESS("退款成功", "SUCCESS"),
    CLOSED("退款关闭", "CLOSED"),
    PROCESSING("退款处理中", "PROCESSING"),
    ABNORMAL("退款异常", "ABNORMAL")
    ;

    private final String name;

    private final String status;
}
