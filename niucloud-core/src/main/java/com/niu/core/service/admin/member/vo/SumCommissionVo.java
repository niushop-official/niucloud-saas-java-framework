package com.niu.core.service.admin.member.vo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Member视图
 */
@Data
public class SumCommissionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal totalCommission;  // 累计提现
    private BigDecimal commission;  // 未提现
    private BigDecimal withdrawnCommission;  // 已提现
    private BigDecimal commissionCashOuting;  // 提现中
}
