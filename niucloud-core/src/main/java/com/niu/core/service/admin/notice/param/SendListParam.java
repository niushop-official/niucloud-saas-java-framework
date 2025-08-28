package com.niu.core.service.admin.notice.param;

import lombok.Data;

@Data
public class SendListParam {
    private String content;

    private String mobile;

    private String smsStatus;
}
