package com.niu.core.service.api.member.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class AccountBalanceFlowParam implements ApiBaseParam {

    private String fromType;

    private String accountType = "balance";

    private String tradeType;

    private String[] createTime;

    private String accountDataGt;

    private String accountDataLt;

    private String keyword;

}
