package com.niu.shop.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum OrderRefundTypeEnum {
    ONLY_REFUND("仅退款", 1),
    RETURN_AND_REFUND_GOODS("退款退货", 2);

    private final String name;

    private final Integer type;

    public static String getNameByType(Integer type){
        for (OrderRefundTypeEnum item: OrderRefundTypeEnum.values()) {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }

    public static Map<Integer, String> getMap(){
        Map<Integer, String> map = new HashMap<>();
        for (OrderRefundTypeEnum item: OrderRefundTypeEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }
}
