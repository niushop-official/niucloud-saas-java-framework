package com.niu.shop.enums.goods;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum GoodsRankTypeEnum {
    DAY("day", "天"),
    WEEK("week", "周"),
    MONTH("month", "月"),
    QUARTER("quarter", "季度"),
    ;

    private final String value;
    private final String name;

    GoodsRankTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getNameByValue(String value) {
        return Arrays.stream(GoodsRankTypeEnum.values())
                .filter(t -> t.getValue().equals(value))
                .map(GoodsRankTypeEnum::getName)
                .findFirst()
                .orElse("");
    }

    public static GoodsRankTypeEnum getEnumByValue(String value) {
        return Arrays.stream(GoodsRankTypeEnum.values())
                .filter(t -> t.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }

}
