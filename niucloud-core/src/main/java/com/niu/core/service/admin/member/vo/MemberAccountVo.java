package com.niu.core.service.admin.member.vo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Member视图
 */
@Data
public class MemberAccountVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer point;  // 可用积分
    private Integer pointGet;  // 累计获取积分
    private BigDecimal balance;  // 可用余额
    private BigDecimal balanceGet;  // 累计获取余额
    private BigDecimal money;  // 可用余额（可提现）
    private BigDecimal moneyGet;  // 累计获取余额（可提现）
    private BigDecimal moneyCashOuting;  // 提现中余额（可提现）
    private Integer growth;  // 成长值
    private Integer growthGet;  // 累计获得成长值
    private BigDecimal commission;  // 当前佣金
    private BigDecimal commissionGet;  // 佣金获取
    private BigDecimal commissionCashOuting;  // 提现中佣金
}
