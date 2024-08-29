package com.niu.core.service.admin.member.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * MemberCashOutAccount列表视图
 */
@Data
public class MemberCashOutAccountListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer accountId;  // 
    private Integer siteId;  // 站点id
    private Integer memberId;  // 会员id
    private String accountType;  // 账户类型
    private String bankName;  // 银行名称
    private String realname;  // 真实名称
    private String createTime; // 创建时间
    private String updateTime; // 修改时间
    private String accountNo;  // 提现账户

}
