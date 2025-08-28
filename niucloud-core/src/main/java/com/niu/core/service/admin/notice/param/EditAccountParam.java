package com.niu.core.service.admin.notice.param;

import lombok.Data;

@Data
public class EditAccountParam {
    private String newMobile;

    private String mobile;

    private String code;

    private String key;

    private String signature;
}
