package com.niu.core.enums.diy_form;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 是否允许修改内容
 */
@Getter
@AllArgsConstructor
public enum DiyFormEditEnum {
    NO(0),
    YES(1);

    private final Integer code;
}
