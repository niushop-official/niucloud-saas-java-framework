package com.niu.shop.enums.goods;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum GoodsRuleTypeEnum {
    SALE("sale", "按照销量"),
    COLLECT("collect", "按收藏数"),
    EVALUATE("evaluate", "按评价数"),
    ACCESS("access", "按照浏览量"),
    ;

    private final String value;
    private final String name;

    GoodsRuleTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getNameByValue(String value) {
        return Arrays.stream(GoodsRuleTypeEnum.values())
                .filter(t -> t.getValue().equals(value))
                .map(GoodsRuleTypeEnum::getName)
                .findFirst()
                .orElse("");
    }
}
