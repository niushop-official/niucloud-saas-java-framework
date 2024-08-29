package com.niu.core.enums.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum SignStatusEnum {
    NOT_SIGN("未签到", 0),
    SIGNED("已签到", 1);

    private final String name;

    private final Integer status;

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for(SignStatusEnum item : SignStatusEnum.values())
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
        for(SignStatusEnum item : SignStatusEnum.values())
        {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }
}
