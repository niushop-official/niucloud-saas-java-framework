package com.niu.shop.enums.marketing;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ShopManjianCouponType {
    ALL(1, "通用券"),
    CATEGORY(2, "品类券"),
    GOODS(3, "商品券"),
    ;

    private final int value;
    private final String name;

    ShopManjianCouponType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getNameByValue(Integer value) {
        return Arrays.stream(ShopManjianCouponType.values())
                .filter(type -> ObjectUtil.equal(type.getValue(), value))
                .map(ShopManjianCouponType::getName)
                .findFirst()
                .orElse("");
    }

}
