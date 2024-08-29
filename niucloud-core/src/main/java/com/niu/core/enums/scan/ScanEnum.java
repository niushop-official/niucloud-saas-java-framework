package com.niu.core.enums.scan;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 扫码枚举类
 */
@Getter
@AllArgsConstructor
public enum ScanEnum {
    WAIT("wait"),
    SUCCESS("success"),
    FAIL("fail");

    private final String code;
}
