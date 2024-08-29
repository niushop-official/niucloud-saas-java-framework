package com.niu.core.service.admin.member.param;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * 会员提现账户参数
*/
@Data
public class MemberCashOutAccountParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "accountId参数缺失")
    private Integer accountId;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "memberId参数缺失")
    private Integer memberId;

    @NotNull(message = "accountType参数缺失")
    private String accountType;

    @NotNull(message = "bankName参数缺失")
    private String bankName;

    @NotNull(message = "realname参数缺失")
    private String realname;

    @NotNull(message = "accountNo参数缺失")
    private String accountNo;

}
