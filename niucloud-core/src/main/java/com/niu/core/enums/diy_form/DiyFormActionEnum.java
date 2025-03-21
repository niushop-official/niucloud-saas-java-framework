package com.niu.core.enums.diy_form;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 表单操作类型
 */
@Getter
@AllArgsConstructor
public enum DiyFormActionEnum {
    TEXT("text"),
    VOUCHER("voucher");

    private final String code;
}
