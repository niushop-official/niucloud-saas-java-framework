package com.niu.core.enums.sys;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleStatusEnum {

    ON("开启","1"),
    OFF("关闭", "0"),
    ;
    private final String name;
    private final String code;
}
