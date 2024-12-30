package com.niu.shop.enums.marketing;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ShopManjianGoodsTypeEnum {
    ALL_GOODS("all_goods", "全部商品参与"),
    SELECTED_GOODS("selected_goods", "指定商品参与"),
    SELECTED_GOODS_NOT("selected_goods_not", "指定商品不参与"),
    ;

    private final String value;
    private final String name;

    ShopManjianGoodsTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getNameByValue(String value) {
        return Arrays.stream(ShopManjianGoodsTypeEnum.values())
                .filter(type -> ObjectUtil.equal(value, type.getValue()))
                .map(ShopManjianGoodsTypeEnum::getName)
                .findFirst()
                .orElse("");
    }

    public static ShopManjianGoodsTypeEnum getEnumByValue(String value) {
        return Arrays.stream(ShopManjianGoodsTypeEnum.values())
                .filter(type -> ObjectUtil.equal(value, type.getValue()))
                .findFirst()
                .orElse(null);
    }

}
