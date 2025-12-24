package com.niu.shop.enums.active;

import com.niu.core.common.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MarketingActivityTypeEnum {
    PINTUAN("pintuan", "拼团"),
    SECKILL("seckill", "秒杀"),
    SHOP("shop", "商城"),
    EXCHANGE("exchange", "兑换"),
    GIFTCARD("giftcard", "礼品卡"),
    NEWCOMER_DISCOUNT("newcomer_discount", "新人专享"),
    DISCOUNT("discount", "限时折扣"),
    MANJIANSONG("manjiansong", "满减送"),
    FRIEND_HELP("friend_help", "好友助力");

    private final String type;
    private final String name;

    /**
     * 通过type获取名称
     *
     * @param type 活动类型
     * @return 活动名称
     */
    public static String getNameByType(String type) {
        if (StringUtils.isEmpty(type)){
            return "";
        }
        for (MarketingActivityTypeEnum item : MarketingActivityTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }

    public static String getNameByTypeDefaultShop(String type) {
        if (StringUtils.isEmpty(type)){
            return SHOP.getName();
        }
        for (MarketingActivityTypeEnum item : MarketingActivityTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }
}