package com.niu.core.component.sms.model;

import com.niu.core.enums.sys.SmsStatusEnum;
import lombok.Data;

@Data
public class SmsSendResult {

    private SmsStatusEnum status;

    private String failReason;

    private Object original;

}
