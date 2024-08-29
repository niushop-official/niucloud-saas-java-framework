package com.niu.core.service.admin.notice.param;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class SmsTypeParam {
    private static final long serialVersionUID = 1L;

    private String smsType;
    private Integer isUse;
    private String name;
    private String component;
    private JSONObject params;
}
