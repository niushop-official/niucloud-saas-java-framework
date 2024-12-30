package com.niu.core.service.core.member.param;

import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class MemberCashOutApplyParam {

    private Integer siteId;

    private Integer memberId;

    private String accountType = AccountTypeEnum.MONEY.getType();

    @NotNull(message = "申请金额不能为空")
    private BigDecimal applyMoney;

    @NotNull(message = "转账方式不能为空")
    private String transferType;

    private Integer accountId = 0;

}
