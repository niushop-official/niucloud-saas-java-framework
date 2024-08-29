package com.niu.core.service.core.niucloud.vo;
import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 */
@Data
public class NiucloudConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String authCode = "";

    private String authSecret = "";
}
