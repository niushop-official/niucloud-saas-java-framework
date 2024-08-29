package com.niu.core.service.admin.member.vo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Member视图
 */
@Data
public class SumBalanceVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal balance;  // 可用余额
    private BigDecimal money;  // 可用余额（可提现）
}
