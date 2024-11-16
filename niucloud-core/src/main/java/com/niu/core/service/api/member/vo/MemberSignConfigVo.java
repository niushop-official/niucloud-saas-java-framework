package com.niu.core.service.api.member.vo;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class MemberSignConfigVo {

    private Boolean isUse = false;

    private Integer signPeriod = 0;

    private JSONObject dayAward = new JSONObject();

    private JSONArray continueAward = new JSONArray();

    private String ruleExplain = "";

    private Boolean isSign;

    private Integer days;
}
