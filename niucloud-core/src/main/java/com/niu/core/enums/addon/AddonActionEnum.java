package com.niu.core.enums.addon;

import java.util.HashMap;
import java.util.Map;

/**
 * 插件操作枚举类
 */
public enum AddonActionEnum {
    INSTALL("安装", "install"),
    UNINSTALL("卸载", "uninstall"),
    UPDATE("更新", "update"),

    APP("应用", "app"),
    ADDON("插件", "addon"),
    ;
    String name;
    String code;
    AddonActionEnum(String name, String code){
        this.name = name;
        this.code = code;
    }

    public String getName(){
        return this.name;
    }

    public String getCode() {
        return code;
    }



    public static Map<String, String> getMap(){
        Map<String, String> map = new HashMap<>();
        for(AddonActionEnum item : AddonActionEnum.values()){
            map.put(item.getCode(), item.getName());
        }
        return map;
    }
}
