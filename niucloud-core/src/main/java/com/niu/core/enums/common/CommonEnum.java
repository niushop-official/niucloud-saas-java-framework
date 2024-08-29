package com.niu.core.enums.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * admin管理菜单
 */
@Getter
@AllArgsConstructor
public enum CommonEnum {

    UNKNOWN(0),
    MAN(1),
    WOMAN(2),
    ;
    private final Integer code;
}
