package com.niu.core.enums.diy_form;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 扫码枚举类
 */
@Getter
@AllArgsConstructor
public enum DiyFormLimitEnum {
    WRITE_NUM_NO_LIMIT("no_limit"),
    WRITE_NUM_DIY("diy");

    private final String code;
}
