package com.niu.core.enums.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TemplateAuditStatus {
    TEMPLATE_NOT_REPORT(-1, "未报备"),
    TEMPLATE_WAIT(1, "待审核"),
    TEMPLATE_PASS(2, "审核通过"),
    TEMPLATE_REFUSE(3, "审核不通过"),
    TEMPLATE_NEED_EDIT(-2, "需修改报备参数"),
    TEMPLATE_STATUS_AGAIN_REPORT(-3, "需重新报备"),
    TEMPLATE_NEED_PULL(-4, "需拉取同步");

    private final Integer code;
    private final String description;


    public static TemplateAuditStatus getByCode(Integer code) {
        for (TemplateAuditStatus status : TemplateAuditStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }
}
