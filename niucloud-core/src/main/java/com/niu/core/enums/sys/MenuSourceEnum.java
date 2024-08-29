package com.niu.core.enums.sys;

import com.niu.core.common.utils.language.LanguageUtils;

/**
 * 菜单枚举
 */
public enum MenuSourceEnum {

    SYSTEM(LanguageUtils.get("dict_menu.source_system"), "system"),
    CREATE(LanguageUtils.get("dict_menu.source_create"), "create"),
    GENERATOR(LanguageUtils.get("dict_menu.source_generator"), "generator"),
    ;

    String name;
    String code;

    MenuSourceEnum(String name, String code){
        this.name=name;
        this.code=code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
