package com.niu.shop.enums.goods;

import lombok.Getter;

@Getter
public enum GoodsStatisticTypeEnum {
    ACCESS_NUM("access_num", "访问次数"),
    GOODS_VISIT_MEMBER_COUNT("cart_num", "加入购物车数量"),
    CART_NUM("sale_num", "商品销量"),
    SALE_NUM("pay_num", "支付件数"),
    PAY_NUM("collect_num", "收藏数量"),
    PAY_MONEY("pay_money", "支付总金额"),
    COLLECT_NUM("goods_visit_member_count", "访客数"),
    ;

    private final String value;
    private final String name;

    GoodsStatisticTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }


}
