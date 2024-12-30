package com.niu.core.enums.pay;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayMainType {
    MEMBER("member", "会员"),
    USER("user", "用户");

    private final String type;
    private final String name;
}
