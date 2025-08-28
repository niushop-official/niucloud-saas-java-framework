package com.niu.core.enums.pay;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 退款状态枚举
 */
@Getter
@AllArgsConstructor
public enum RefundStatusEnum {
    SUCCESS("退款成功", "success"),
    WAIT("待退款", "wait"),
    DEALING("退款处理中", "dealing"),
    FAIL("退款失败", "fail"),
    CANCEL("已取消", "cancel"),
    ;

    private final String name;

    private final String status;

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (RefundStatusEnum item:  RefundStatusEnum.values()) {
            map.put(item.getStatus(), item.getName());
        }
        return map;
    }

    public static String getNameByStatus(String status){
        for (RefundStatusEnum item:  RefundStatusEnum.values()) {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }
}
