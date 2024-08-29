package com.niu.core.enums.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 性别枚举类
 */
@Getter
@AllArgsConstructor
public enum SexEnum {
    UNKNOWN("未知", 0),
    MAN("男", 1),
    WOMAN("女", 2);

    private final String name;

    private final Integer value;

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for(SexEnum item : SexEnum.values())
        {
            map.put(item.getValue(), item.getName());
        }
        return map;
    }

    /**
     * 通过sex获取当前名称
     * @param sex
     * @return
     */
    public static String getNameBySex(Integer sex){
        for(SexEnum item : SexEnum.values())
        {
            if(item.getValue().equals(sex))
            {
                return item.getName();
            }
        }
        return "";
    }
}
