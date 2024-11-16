package com.niu.shop.enums.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum CouponMemberStatusEnum {
    WAIT_USE(1,"待使用"),
    USED(2,"已使用"),
    EXPIRE(3,"已过期"),
    INVALID(4,"已失效");

    private final Integer status;

    private final String name;

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for(CouponMemberStatusEnum item : CouponMemberStatusEnum.values())
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
        for(CouponMemberStatusEnum item : CouponMemberStatusEnum.values())
        {
            if(item.getStatus() == status)
            {
                return item.getName();
            }
        }
        return "";
    }
}
