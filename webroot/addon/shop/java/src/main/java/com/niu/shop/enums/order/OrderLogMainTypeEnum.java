package com.niu.shop.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum OrderLogMainTypeEnum {
    STORE("商家", "store"),
    MEMBER("会员", "member"),
    SYSTEM("系统", "system");

    private final String name;

    private final String type;

    public static String getNameByType(String type){
        for (OrderLogMainTypeEnum item: OrderLogMainTypeEnum.values()) {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }
}
