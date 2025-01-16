package com.niu.shop.enums.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum OrderBatchDeliveryStatusEnum {
    PROCESSING(1, "处理中"),
    FINISH(2, "已完成"),
    FAIL(3, "任务失败");

    private final Integer value;
    private final String name;

    public static String getNameByValue(Integer value) {
        return Arrays.stream(OrderBatchDeliveryStatusEnum.values())
                .filter(o -> o.getValue().equals(value))
                .findAny()
                .map(OrderBatchDeliveryStatusEnum::getName)
                .orElse("");
    }
}
