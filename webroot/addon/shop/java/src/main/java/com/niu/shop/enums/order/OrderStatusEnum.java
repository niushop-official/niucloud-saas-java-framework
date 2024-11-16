package com.niu.shop.enums.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {
    WAIT_PAY("待支付", 1, 0),
    WAIT_DELIVERY("待发货", 2, 0),
    WAIT_TAKE("待收货", 3, 0),
    FINISH("已完成", 5, 0),
    CLOSE("已关闭", -1, 0);

    private final String name;

    private final Integer status;

    private final Integer isRefund;

    public static String getNameByStatus(Integer status){
        for (OrderStatusEnum item: OrderStatusEnum.values()) {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }

    public static Map<String, Object> getMapByStatus(Integer status){
        Map<String, Object> map = new HashMap<>();
        for (OrderStatusEnum item: OrderStatusEnum.values()) {
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
        for (OrderStatusEnum item : OrderStatusEnum.values()) {
            Map<String, Object> mapItem = new HashMap<>();
            mapItem.put("name", item.getName());
            mapItem.put("status", item.getStatus());
            map.put(item.getStatus(), mapItem);
        }
        return map;
    }
}
