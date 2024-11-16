package com.niu.shop.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum InvoiceTypeEnum {
    COMMON("普票", 1),
    OPEN("专票", 2);

    private final String name;

    private final Integer type;


    public static String getNameByType(Integer type){
        for (InvoiceTypeEnum item: InvoiceTypeEnum.values()) {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }

    public static Map<Integer, Map<String, Object>> getMap() {
        Map<Integer, Map<String, Object>> map = new HashMap<>();
        for (InvoiceTypeEnum item : InvoiceTypeEnum.values()) {
            Map<String, Object> mapItem = new HashMap<>();
            mapItem.put("name", item.getName());
            mapItem.put("type", item.getType());
            map.put(item.getType(), mapItem);
        }
        return map;
    }
}
