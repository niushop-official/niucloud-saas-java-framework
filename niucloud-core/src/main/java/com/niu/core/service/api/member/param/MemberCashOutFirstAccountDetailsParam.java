package com.niu.core.service.api.member.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class MemberCashOutFirstAccountDetailsParam implements ApiBaseParam {

    private Integer accountId;

    private String accountType;

}
