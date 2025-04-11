package com.niu.core.entity.member;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员提现账户实体
 */
@Data
public class MemberCashOutAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="account_id", type= IdType.AUTO)
    private Integer accountId;

    private Integer siteId;

    private Integer memberId;

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

    /** 收款码 */
    private String transferPaymentCode;
}