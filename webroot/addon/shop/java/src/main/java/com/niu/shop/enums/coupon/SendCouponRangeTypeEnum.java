package com.niu.shop.enums.coupon;

import com.niu.core.common.exception.CommonException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum SendCouponRangeTypeEnum {
    ALL("all", "全部会员"),
    MEMBER("member", "选择会员"),
    MEMBER_LEVEL("member_level", "按会员等级"),
    MEMBER_LABEL("member_label", "按会员标签");

    private final String code;

    private final String name;

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (SendCouponRangeTypeEnum item : SendCouponRangeTypeEnum.values()) {
            map.put(item.getCode(), item.getName());
        }
        return map;
    }

    /**
     * 通过status获取当前名称
     *
     * @param code
     * @return
     */
    public static String getNameByStatus(String code) {
        for (SendCouponRangeTypeEnum item : SendCouponRangeTypeEnum.values()) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static SendCouponRangeTypeEnum fromCode(String code) {
        for (SendCouponRangeTypeEnum type : SendCouponRangeTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new CommonException("未知的优惠券发送范围类型: " + code);
    }
}
