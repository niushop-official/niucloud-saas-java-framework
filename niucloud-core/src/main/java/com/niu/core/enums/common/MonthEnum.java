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
public enum MonthEnum {
    JAN("1月", 1),
    FEB("2月", 2),
    MAR("3月", 3),
    APR("4月", 4),
    MAY("5月", 5),
    JUN("6月", 6),
    JUL("7月", 7),
    AUG("8月", 8),
    SEPT("9月", 9),
    OCT("10月", 10),
    NOV("11月", 11),
    DEC("12月", 12);

    private final String name;

    private final Integer value;

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for(MonthEnum item : MonthEnum.values())
        {
            map.put(item.getValue(), item.getName());
        }
        return map;
    }

    /**
     * 通过value获取当前名称
     * @param value
     * @return
     */
    public static String getNameByValue(Integer value){
        for(MonthEnum item : MonthEnum.values())
        {
            if(item.getValue().equals(value))
            {
                return item.getName();
            }
        }
        return "";
    }
}
