package com.niu.shop.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum InvoiceStatusEnum {
    WAIT_OPEN("未生效", 1),
    OPEN("已生效", 2);

    private final String name;

    private final Integer status;


    public static String getNameByStatus(Integer status){
        for (InvoiceStatusEnum item: InvoiceStatusEnum.values()) {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }

    public static Map<String, Object> getMapByStatus(Integer status){
        Map<String, Object> map = new HashMap<>();
        for (InvoiceStatusEnum item: InvoiceStatusEnum.values()) {
            if(item.getStatus().equals(status))
            {
                map.put("name", item.getName());
                map.put("status", item.getStatus());
            }
        }
        return map;
    }

    public static Map<Integer, Map<String, Object>> getMap() {
        Map<Integer, Map<String, Object>> map = new HashMap<>();
        for (InvoiceStatusEnum item : InvoiceStatusEnum.values()) {
            Map<String, Object> mapItem = new HashMap<>();
            mapItem.put("name", item.getName());
            mapItem.put("status", item.getStatus());
            map.put(item.getStatus(), mapItem);
        }
        return map;
    }
}
