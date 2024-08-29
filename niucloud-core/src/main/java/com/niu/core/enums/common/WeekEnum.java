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
public enum WeekEnum {
    MON("周一", 1),
    TUE("周二", 2),
    WED("周三", 3),
    THUR("周四", 4),
    FRI("周五", 5),
    SAT("周六", 6),
    SUN("周日", 0);

    private final String name;

    private final Integer value;

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for(WeekEnum item : WeekEnum.values())
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
        for(WeekEnum item : WeekEnum.values())
        {
            if(item.getValue().equals(sex))
            {
                return item.getName();
            }
        }
        return "";
    }
}
