package com.niu.core.enums.notice;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;


@Getter
@AllArgsConstructor
public enum SignAuditStatusEnum {

    WAIT(1, "待审核"),
    PASS(2, "审核通过"),
    REFUSE(3, "审核不通过");

    private final Integer code;
    private final String description;

    public static SignAuditStatusEnum fromCode(int code) {
        for (SignAuditStatusEnum type : SignAuditStatusEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (SignAuditStatusEnum type : SignAuditStatusEnum.values()) {
            map.put(type.getCode().toString(), type.getDescription());
        }
        return map;
    }
}
