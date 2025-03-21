package com.niu.core.enums.diy_form;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 核销有效期
 */
@Getter
@AllArgsConstructor
public enum DiyFormVerifyEnum {
    NO_LIMIT("no_limit"),
    SPECIFY_TIME("specify_time"),
    VALIDITY_TIME("validity_time");

    private final String code;
}
