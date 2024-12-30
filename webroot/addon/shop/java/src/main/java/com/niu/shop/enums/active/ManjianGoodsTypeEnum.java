package com.niu.shop.enums.active;

import lombok.Getter;

@Getter
public enum ManjianGoodsTypeEnum {
    ALL_GOODS("all_goods", "全部商品参与"),
    SELECTED_GOODS("selected_goods", "指定商品参与"),
    SELECTED_GOODS_NOT("selected_goods_not", "指定商品不参与"),
    ;

    private final String value;
    private final String name;

    ManjianGoodsTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }
}
