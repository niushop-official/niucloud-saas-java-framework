package com.niu.core.common.component.sms.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
// @Builder
public class SmsSend {

    // 发送的目标手机号
    private String mobile;

    // 使用的模板ID
    private String templateId;

    // 发送关键字
    private String keyword;

    // 发送的消息
    private String message;

}
