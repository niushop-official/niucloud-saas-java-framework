package com.niu.shop.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum OrderDeliveryTypeEnum {
    EXPRESS("express","物流配送"),
    LOCAL_DELIVERY("local_delivery","同城配送"),
    STORE("store","门店自提"),
    NONE_EXPRESS("none_express","无需物流"),
    VIRTUAL("virtual","虚拟发货");

    private final String type;

    private final String name;

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for(OrderDeliveryTypeEnum item : OrderDeliveryTypeEnum.values())
        {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    public static Map<String, String> getMapByType(String deliveryType) {
        Map<String, String> map = new HashMap<>();
        if (deliveryType.equals(OrderDeliveryTypeEnum.EXPRESS.getType())) {
            map.put(EXPRESS.type, EXPRESS.name);
            map.put(NONE_EXPRESS.type, NONE_EXPRESS.name);
        } else if (deliveryType.equals(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
            map.put(LOCAL_DELIVERY.type, LOCAL_DELIVERY.name);
        } else if (deliveryType.equals(OrderDeliveryTypeEnum.STORE.getType())) {
            map.put(STORE.type, STORE.name);
        } else if (deliveryType.equals(OrderDeliveryTypeEnum.VIRTUAL.getType())) {
            map.put(VIRTUAL.type, VIRTUAL.name);
        } else {
            return getMap();
        }
        return map;
    }

    /**
     * 通过type获取当前名称
     * @param type
     * @return
     */
    public static String getNameByType(String type){
        for(OrderDeliveryTypeEnum item : OrderDeliveryTypeEnum.values())
        {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }

    public static Optional<OrderDeliveryTypeEnum> getEnumByType(String type){
        return Arrays.stream(OrderDeliveryTypeEnum.values())
                .filter(e -> e.getType().equals(type))
                .findFirst();
    }
}
