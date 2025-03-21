package com.niu.core.enums.diy_form;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 表单添加方式
 */
@Getter
@AllArgsConstructor
public enum DiyFormAddTypeEnum {
    WRITE_WAY_NO_LIMIT("no_limit"),
    WRITE_WAY_SCAN("scan"),
    WRITE_WAY_URL("url");

    private final String code;
}
