package com.niu.shop.enums.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum CouponTypeEnum {
    ALL(1,"通用券"),
    CATEGORY(2,"品类券"),
    GOODS(3,"商品券");

    private final Integer type;

    private final String name;

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for(CouponTypeEnum item : CouponTypeEnum.values())
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
        for(CouponTypeEnum item : CouponTypeEnum.values())
        {
            if(item.getType() == type)
            {
                return item.getName();
            }
        }
        return "";
    }
}
