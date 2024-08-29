package com.niu.core.common.loader.sms;

import com.niu.core.enums.sys.SmsStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class SendResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private SmsStatusEnum status;

    private String failReason;

    private Object original;
}
