package com.niu.shop.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum OrderRefundSourceEnum {
    APPLY("买家申请退款", 1),
    ACTIVE_REFUND("卖家主动退款", 2);

    private final String name;

    private final Integer type;

    public static String getNameByStatus(Integer type){
        for (OrderRefundSourceEnum item: OrderRefundSourceEnum.values()) {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }

    public static Map<Integer, String> getMap(){
        Map<Integer, String> map = new HashMap<>();
        for (OrderRefundSourceEnum item: OrderRefundSourceEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }
}
