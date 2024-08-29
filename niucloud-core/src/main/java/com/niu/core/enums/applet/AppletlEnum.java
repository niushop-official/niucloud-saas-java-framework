package com.niu.core.enums.applet;

/**
 * 小程序版本枚举
 */
public enum AppletlEnum {
    ON(1, "启用"),
    OFF(2, "下架");
    Integer code;
    String name;
    AppletlEnum(Integer code, String name){
        this.name=name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public Integer getCode(){
        return code;
    }
}
