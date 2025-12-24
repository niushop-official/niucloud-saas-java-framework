package com.niu.shop.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum InvoiceHeaderTypeEnum {
    PERSON("个人", 1),
    COMPANY("公司", 2);

    private final String name;

    private final Integer type;


    public static String getNameByType(Integer type) {
        if (type == null){
            return "";
        }
        for (InvoiceHeaderTypeEnum item : InvoiceHeaderTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }

    public static Map<String, Object> getMapByType(Integer type) {
        Map<String, Object> map = new HashMap<>();
        for (InvoiceHeaderTypeEnum item : InvoiceHeaderTypeEnum.values()) {
            if (item.getType().equals(type)) {
                map.put("name", item.getName());
                map.put("type", item.getType());
            }
        }
        return map;
    }

    public static Map<Integer, Map<String, Object>> getMap() {
        Map<Integer, Map<String, Object>> map = new HashMap<>();
        for (InvoiceHeaderTypeEnum item : InvoiceHeaderTypeEnum.values()) {
            Map<String, Object> mapItem = new HashMap<>();
            mapItem.put("name", item.getName());
            mapItem.put("type", item.getType());
            map.put(item.getType(), mapItem);
        }
        return map;
    }
}
