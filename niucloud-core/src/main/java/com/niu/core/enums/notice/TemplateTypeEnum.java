package com.niu.core.enums.notice;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;


@Getter
@AllArgsConstructor
public enum TemplateTypeEnum {

    VERIFY_CODE(1, "验证码"),
    INDUSTRY_NOTICE(2, "行业通知"),
    SEM(3, "营销推广");

    private final Integer code;
    private final String description;

    public static TemplateTypeEnum fromCode(int code) {
        for (TemplateTypeEnum type : TemplateTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (TemplateTypeEnum type : TemplateTypeEnum.values()) {
            map.put(type.getCode().toString(), type.getDescription());
        }
        return map;
    }
}
