package com.niu.core.service.admin.member.vo;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Member视图
 */
@Data
public class SignConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean isUse = false;  // 是否开启
    private Integer signPeriod = 0;  // 签到周期
    private JSONObject dayAward = new JSONObject();  // 日签奖励
    private JSONArray continueAward = new JSONArray();  // 连签奖励
    private String ruleExplain = "";  // 规则说明
}
