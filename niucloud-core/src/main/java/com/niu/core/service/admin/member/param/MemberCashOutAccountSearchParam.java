package com.niu.core.service.admin.member.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员提现账户参数
 */
@Data
public class MemberCashOutAccountSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer memberId;

    private String accountType;

    private String bankName;

    private String realname;

    private String accountNo;

}
