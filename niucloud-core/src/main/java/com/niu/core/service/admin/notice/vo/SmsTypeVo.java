package com.niu.core.service.admin.notice.vo;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.util.Map;

@Data
public class SmsTypeVo {
    private static final long serialVersionUID = 1L;

    private String smsType;
    private Integer isUse = 0;
    private String name;
    private String component;
    private Map<String, SmsTypeParamVo> params;
}
