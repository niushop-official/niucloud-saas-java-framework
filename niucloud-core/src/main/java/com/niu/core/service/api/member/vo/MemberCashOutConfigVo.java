package com.niu.core.service.api.member.vo;

import lombok.Data;

@Data
public class MemberCashOutConfigVo {

    // 是否启用提现
    private Integer isOpen;

    // 提现方式
    private String transferType;

    // 最低提现金额
    private Integer min;

    // 最高提现金额
    // private Integer max;

    // 手续费比率
    private Integer rate;

    // 是否自动审核
    private Integer isAutoVerify;

    // 是否自动转账
    private Integer isAutoTransfer;

}
