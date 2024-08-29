package com.niu.core.enums.sys;

import com.niu.core.common.utils.language.LanguageUtils;

/**
 * 菜单状态枚举
 */
public enum MenuStatusEnum {

    ON(LanguageUtils.get("dict_menu.status_on"), 1),
    OFF(LanguageUtils.get("dict_menu.status_off"), 0),
    ;

    String name;
    Integer code;

    MenuStatusEnum(String name, Integer code){
        this.name=name;
        this.code=code;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }
}
