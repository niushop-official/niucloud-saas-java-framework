package com.niu.shop.enums.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CouponSendStatus {

    SEND_STATUS_WAIT("wait", "待发放"),
    SEND_STATUS_PROGRESS("progress", "发放中"),
    SEND_STATUS_SUCCESS("success", "发放成功"),
    SEND_STATUS_FINISH("finish", "发放完成");


    private final String code;
    private final String name;

    public static String getNameByCode(String code) {
        for (CouponSendStatus couponSendStatus : CouponSendStatus.values()) {
            if (couponSendStatus.getCode().equals(code)) {
                return couponSendStatus.getName();
            }
        }
        return "";
    }
}
