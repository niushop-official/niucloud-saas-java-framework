package com.niu.shop.enums.active;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActiveClassEnum {
    DISCOUNT("discount", "限时折扣"),
    EXCHANGE("exchange", "积分兑换"),
    MANJIANSONG("manjiansong", "满减送"),
    NEWCOMER_DISCOUNT("newcomer_discount", "新人专享"),
    GIFTCARD("giftcard  ", "礼品卡"),
    ;


    private final String type;

    private final String name;

    /**
     * 通过type获取当前名称
     *
     * @param type
     * @return
     */
    public static String getNameByType(String type) {
        for (ActiveClassEnum item : ActiveClassEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }
}
