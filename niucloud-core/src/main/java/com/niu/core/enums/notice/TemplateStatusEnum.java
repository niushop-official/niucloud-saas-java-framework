package com.niu.core.enums.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum TemplateStatusEnum {
    TEMPLATE_NEED_PULL(-4, "需拉取同步"),
    TEMPLATE_AUDIT_STATUS_NEED_AGAIN_REPORT(-3, "需重新报备"),
    TEMPLATE_AUDIT_STATUS_NEED_EDIT(-2, "需修改报备参数"),
    TEMPLATE_AUDIT_STATUS_NOT_REPORT(-1, "未报备"),
    TEMPLATE_AUDIT_STATUS_WAIT(1, "待审核"),
    TEMPLATE_AUDIT_STATUS_PASS(2, "审核通过"),
    TEMPLATE_AUDIT_STATUS_REFUSE(3, "审核不通过");

    private final Integer code;
    private final String description;

    public static TemplateStatusEnum fromCode(int code) {
        for (TemplateStatusEnum status : TemplateStatusEnum.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (TemplateStatusEnum status : TemplateStatusEnum.values()) {
            map.put(String.valueOf(status.getCode()), status.getDescription());
        }
        return map;
    }
}
