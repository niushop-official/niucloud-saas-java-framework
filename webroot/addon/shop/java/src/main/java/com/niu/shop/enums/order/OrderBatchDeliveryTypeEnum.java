package com.niu.shop.enums.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum OrderBatchDeliveryTypeEnum {
    DELIVERY("delivery", "批量导入发货");

    private final String value;
    private final String name;

    public static String getNameByValue(String value) {
        return Arrays.stream(OrderBatchDeliveryTypeEnum.values())
                .filter(o -> o.getValue().equals(value))
                .findAny()
                .map(OrderBatchDeliveryTypeEnum::getName)
                .orElse("");
    }
}
