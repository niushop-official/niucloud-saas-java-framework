package com.niu.core.service.core.channel.vo;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class AppCompileLogVo {

    private String status = "";

    private String failReason;

    private String filePath;

    private JSONObject buildLog;
}
