package com.niu.shop.enums.active;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActiveTypeEnum {
    MEMBER("member", "会员活动"),
    GOODS("goods", "商品活动"),
    NEWCOMER_DISCOUNT("newcomer_discount", "新人专享"),
    SHOP("shop", "店铺活动");

    private final String type;

    private final String name;

    /**
     * 通过type获取当前名称
     *
     * @param type
     * @return
     */
    public static String getNameByType(String type) {
        for (ActiveTypeEnum item : ActiveTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }
}
