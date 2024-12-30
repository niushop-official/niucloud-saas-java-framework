package com.niu.shop.enums.goods;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum GoodsSourceTypeEnum {
    ALL("all", "全部"),
    GOODS("goods", "指定商品"),
    CATEGORY("category", "指定分类"),
    BRAND("brand", "指定品牌"),
    LABEL("label", "指定标签"),
    ;

    private final String value;
    private final String name;

    GoodsSourceTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getNameByValue(String value) {
        return Arrays.stream(GoodsSourceTypeEnum.values())
                .filter(t -> t.getValue().equals(value))
                .map(GoodsSourceTypeEnum::getName)
                .findFirst()
                .orElse("");
    }

    public static GoodsSourceTypeEnum getEnumByValue(String value) {
        return Arrays.stream(GoodsSourceTypeEnum.values())
                .filter(t -> t.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }

}
