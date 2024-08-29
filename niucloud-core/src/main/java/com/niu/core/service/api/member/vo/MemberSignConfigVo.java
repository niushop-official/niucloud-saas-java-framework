package com.niu.core.service.api.member.vo;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class MemberSignConfigVo {

    private Boolean isUse;

    private Integer signPeriod;

    private JSONObject dayAward;

    private JSONArray continueAward;

    private String ruleExplain;

    private Boolean isSign;

    private Integer days;
}
