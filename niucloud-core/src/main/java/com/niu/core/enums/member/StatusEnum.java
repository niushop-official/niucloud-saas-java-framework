package com.niu.core.enums.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    OFF("锁定", 0),
    ON("正常", 1);

    private final String name;

    private final Integer status;

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for(StatusEnum item : StatusEnum.values())
        {
            map.put(item.getStatus(), item.getName());
        }
        return map;
    }

    /**
     * 通过status获取当前名称
     * @param status
     * @return
     */
    public static String getNameByStatus(Integer status){
        for(StatusEnum item : StatusEnum.values())
        {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }
}
