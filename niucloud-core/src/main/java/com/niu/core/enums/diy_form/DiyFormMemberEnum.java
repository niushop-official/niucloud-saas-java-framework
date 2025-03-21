package com.niu.core.enums.diy_form;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 扫码枚举类
 */
@Getter
@AllArgsConstructor
public enum DiyFormMemberEnum {
    ALL_MEMBER("all_member"),
    SELECTED_MEMBER_LEVEL("selected_member_level"),
    SELECTED_MEMBER_LABEL("selected_member_label");

    private final String code;
}
