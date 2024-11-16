package com.niu.shop.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum OrderDeliveryStatusEnum {
    WAIT_DELIVERY("待发货", "wait_delivery"),
    DELIVERYING("配送中", "delivery"),
    DELIVERY_FINISH("已发货", "delivery_finish"),
    TAKED("已收货", "taked"),
    EXPIRE("已失效", "expire");

    private final String name;

    private final String status;


    public static String getNameByStatus(String status){
        for (OrderDeliveryStatusEnum item: OrderDeliveryStatusEnum.values()) {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }
}
