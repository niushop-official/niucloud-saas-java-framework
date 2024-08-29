package com.niu.core.enums.addon;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 插件操作枚举类
 */
@Getter
@AllArgsConstructor
public enum AddonTypeEnum {
    APP("应用", "app"),
    ADDON("插件", "addon");

    private String name;
    private String type;

    public static Map<String, String> getMap(){
        Map<String, String> map = new HashMap<>();
        for(AddonTypeEnum item : AddonTypeEnum.values()){
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    public static String getNameByType(String type) {
        for(AddonTypeEnum item : AddonTypeEnum.values()){
            if (item.getType().equals(type)) return item.getName();
        }
        return "";
    }
}
