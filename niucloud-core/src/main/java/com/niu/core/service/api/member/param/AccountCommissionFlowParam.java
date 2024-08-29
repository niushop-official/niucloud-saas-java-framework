package com.niu.core.service.api.member.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class AccountCommissionFlowParam implements ApiBaseParam {

    private String accountType = "commission";

    private String fromType;

    private String[] createTime;

    private String accountDataGt;

    private String accountDataLt;

    private String keyword;

}
