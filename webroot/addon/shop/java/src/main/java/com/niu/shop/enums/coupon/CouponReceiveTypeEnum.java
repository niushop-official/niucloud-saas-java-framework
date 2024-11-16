package com.niu.shop.enums.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum CouponReceiveTypeEnum {
    USER(1,"手动领取"),
    GRANT(2,"后台或活动发放");

    private final Integer type;

    private final String name;

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for(CouponReceiveTypeEnum item : CouponReceiveTypeEnum.values())
        {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    /**
     * 通过type获取当前名称
     * @param type
     * @return
     */
    public static String getNameByType(Integer type){
        for(CouponReceiveTypeEnum item : CouponReceiveTypeEnum.values())
        {
            if(item.getType() == type)
            {
                return item.getName();
            }
        }
        return "";
    }
}
