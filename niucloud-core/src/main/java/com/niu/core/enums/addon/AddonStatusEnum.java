package com.niu.core.enums.addon;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 插件状态枚举类
 */
@Getter
@AllArgsConstructor
public enum AddonStatusEnum {
    ON("安装", 1),
    OFF("卸载", 2),
    ;
    private final String name;
    private final Integer code;

    public static Map<Integer, String> getMap(){
        Map<Integer, String> map = new HashMap<>();
        for(AddonStatusEnum item : AddonStatusEnum.values()){
            map.put(item.getCode(), item.getName());
        }
        return map;
    }

    public static String getName(Integer code){
        return getMap().get(code);
    }
}
