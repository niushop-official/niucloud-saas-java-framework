package com.niu.shop.enums.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GoodsPriceTypeEnum {

    ORIGINAL_PRICE("原价", "original_price", 3),
    MEMBER_PRICE("会员价", "member_price", 2),
    DISCOUNT_PRICE("折扣价", "discount_price", 1),
    NEWCOMER_PRICE("新人价", "newcomer_price", 0);

    private final String name;

    private final String type;

    private final Integer sort;
}
