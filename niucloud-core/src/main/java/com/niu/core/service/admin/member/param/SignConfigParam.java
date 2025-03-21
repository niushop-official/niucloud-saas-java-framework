package com.niu.core.service.admin.member.param;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 会员参数
*/
@Data
public class SignConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "is_use")
    private Boolean isUse;  // 是否开启

    @NotNull(message = "sign_period参数缺失")
    private Integer signPeriod;  // 签到周期

    @NotNull(message = "day_award参数缺失")
    private JSONObject dayAward;  // 日签奖励

    @NotNull(message = "continue_award参数缺失")
    private JSONArray continueAward;  // 连签奖励

    @NotNull(message = "rule_explain参数缺失")
    private String ruleExplain;  // 规则说明
}
