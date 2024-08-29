package com.niu.core.service.api.member.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

import java.util.List;

@Data
public class AccountPointFlowParam implements ApiBaseParam {

    // 来源类型
    private String fromType;

    //全部all 收入income 支出disburse
    private String amountType = "all";

    private String accountType = "point";

    private String[] createTime;

    private String accountDataGt;

    private String accountDataLt;

}
