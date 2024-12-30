package com.niu.shop.enums.marketing;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum ShopManjianStatusEnum {
    NOT_ACTIVE(0, "未开始"),
    ACTIVE(1, "进行中"),
    END(2, "已结束"),
    CLOSE(-1, "已关闭"),
    ;

    private final int value;
    private final String name;

    ShopManjianStatusEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getNameByValue(Integer value) {
        return Arrays.stream(ShopManjianStatusEnum.values())
                .filter(status -> status.getValue() == value)
                .map(ShopManjianStatusEnum::getName)
                .findFirst()
                .orElse("");
    }

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for(ShopManjianStatusEnum item : ShopManjianStatusEnum.values())
        {
            map.put(item.getValue(), item.getName());
        }
        return map;
    }

}
