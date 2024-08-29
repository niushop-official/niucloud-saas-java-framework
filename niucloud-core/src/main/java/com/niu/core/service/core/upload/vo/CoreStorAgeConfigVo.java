package com.niu.core.service.core.upload.vo;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;

@Data
public class CoreStorAgeConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String component;
    private String isUse;
    private String name;
    private JSONObject params;
    private String storageType;
}
