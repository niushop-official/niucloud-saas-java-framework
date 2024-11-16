package com.niu.shop.enums.delivery;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ShippingTemplateEnum {
    WEIGHT("weight","按重量"),
    NUM("num","按件"),
    VOLUME("volume","按体积");

    private final String type;

    private final String name;

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for(ShippingTemplateEnum item : ShippingTemplateEnum.values())
        {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    /**
     * 通过type获取当前名称
     * @param type
     * @return
     */
    public static String getNameByType(String type){
        for(ShippingTemplateEnum item : ShippingTemplateEnum.values())
        {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }
}
