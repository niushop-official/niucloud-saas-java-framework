package com.niu.core.service.api.member.param;

import com.niu.core.entity.member.MemberCashOut;
import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class MemberCashOutAccountAddParam implements ApiBaseParam {

    /** 账户类型 */
    private String accountType;

    /** 银行名称 */
    private String bankName;

    /** 真实名称 */
    private String realname;

    /** 创建时间 */
    private Long createTime;

    /** 修改时间 */
    private Long updateTime;

    /** 提现账户 */
    private String accountNo;

}
