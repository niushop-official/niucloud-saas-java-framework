package com.niu.core.service.api.member.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class MemberCashOutApplyParam implements ApiBaseParam {

    private Integer applyMoney;

    private String accountType = "money";

    private String transferType;

    private Integer accountId;

}
