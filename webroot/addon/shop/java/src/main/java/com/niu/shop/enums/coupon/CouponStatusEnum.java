package com.niu.shop.enums.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum CouponStatusEnum {
    WAIT_START(0,"未开始"),
    NORMAL(1,"进行中"),
    EXPIRE(2,"已过期"),
    INVALID(3,"已失效");

    private final Integer status;

    private final String name;

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for(CouponStatusEnum item : CouponStatusEnum.values())
        {
            map.put(item.getStatus(), item.getName());
        }
        return map;
    }

    /**
     * 通过status获取当前名称
     * @param status
     * @return
     */
    public static String getNameByStatus(Integer status){
        for(CouponStatusEnum item : CouponStatusEnum.values())
        {
            if(item.getStatus() == status)
            {
                return item.getName();
            }
        }
        return "";
    }
}
