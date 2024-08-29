package com.niu.core.service.api.member.vo;

import lombok.Data;

@Data
public class MemberCashOutFirstAccountDetailsVo {

    private Integer accountId;

    private Integer siteId;

    private Integer memberId;

    // 账户类型
    private String accountType;

    // 银行名称
    private String bankName;

    // 真实名称
    private String realname;

    // 提现账户
    private String accountNo;

}
